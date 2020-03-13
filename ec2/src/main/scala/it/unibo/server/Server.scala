package it.unibo.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import it.unibo.S3Load
import it.unibo.classifier.ClassifierFactory
import it.unibo.classifier.ClassifierFactory.{MLP, RF}
import it.unibo.client.Client
import it.unibo.converter.PublicDatasetPayloadConverter
import it.unibo.datapreprocessor.DataPreprocessorFactory
import it.unibo.server.model.{Response, User}
import it.unibo.sparksession.SparkConfiguration

import scala.concurrent.{ExecutionContextExecutor, Future}


class Server(client: Client, basePath: String)(implicit sparkConfiguration: SparkConfiguration) {

  val trainers = Seq(ClassifierFactory(MLP), ClassifierFactory(RF))

  implicit val actorSystem: ActorSystem = ActorSystem("sttp-pres")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val dispatcher: ExecutionContextExecutor = actorSystem.dispatcher

  private val serverRoutes: Route =
    pathPrefix("data") {
      get {
        complete(responseToString())
      }
    }

  def start: Future[Unit] = {
    sparkConfiguration.getOrCreateSession
    Http().bindAndHandle(serverRoutes, "0.0.0.0", 80).map(_ => println("Started"))
  }

  private def responseToString(): String = {

    if (S3Load.isS3Folder(basePath))
      Seq("mlp", "rf").foreach(p => S3Load.copyModelFromS3(p, basePath))

    trainers.foreach(t => t.loadModel())
    val publicDataset = client.getPublicDataset
    val dataFrameToClassify = PublicDatasetPayloadConverter.publicDStoDF(publicDataset.Payload)
    val normalized = DataPreprocessorFactory().normalizeToClassify(dataFrameToClassify)
    val results: Seq[(String, String)] = trainers.flatMap(t => t.classify(normalized))

    val response = Response(composeUsers(results),
      success = publicDataset.Success.getOrElse(false),
      errors = publicDataset.Error.getOrElse(""))

    Response.encoder(response).toString()
  }

  private def composeUsers(classifications: Seq[(String, String)]): Seq[User] = {
    val groupedUsers: Map[String, List[(String, String)]] = classifications.toList groupBy {
      case (userid, _) => userid
    }
    val users = groupedUsers map { case (userId, list) =>
      val result = list map { case (_, rating) => rating }
      User(userId, result.headOption.getOrElse(""), result(1))
    }
    users.toList
  }
}
