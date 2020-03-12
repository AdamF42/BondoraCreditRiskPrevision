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

import scala.concurrent.Future


class Server(client: Client, basePath: String)(implicit sparkConfiguration: SparkConfiguration) {

  val trainers = Seq(ClassifierFactory(MLP), ClassifierFactory(RF))

  implicit val actorSystem: ActorSystem = ActorSystem("sttp-pres")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  import actorSystem.dispatcher

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
    val results = trainers.map(t => t.classify(normalized))

    val response = Response(composeUsers(results.head ++ results(1)),
      success = publicDataset.Success.getOrElse(false),
      errors = publicDataset.Error.getOrElse(""))

    Response.encoder(response).toString()
  }

  private def composeUsers(classifications: Seq[(String, String)]): Seq[User] = {
    val groupedUsers: Map[String, List[(String, String)]] = classifications.toList.groupBy(_._1)
    groupedUsers.map(c => User(c._1, c._2.head._2, c._2(1)._2)).toList
  }
}
