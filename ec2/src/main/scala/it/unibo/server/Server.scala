package it.unibo.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import it.unibo.classifier.ClassifierFactory.{MLP, RF}
import it.unibo.classifier.{BaseClassifier, ClassifierFactory}
import it.unibo.client.Client
import it.unibo.converter.PublicDatasetPayloadConverter
import it.unibo.datapreprocessor.DataPreprocessorFactory
import it.unibo.server.model.{Response, User}
import it.unibo.sparksession.SparkConfiguration

import scala.concurrent.Future


class Server(client: Client)(implicit sparkConfiguration: SparkConfiguration) {

  private val mlpTrainer: BaseClassifier = ClassifierFactory(MLP)
  private val rfTrainer: BaseClassifier = ClassifierFactory(RF)

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
    Http().bindAndHandle(serverRoutes, "0.0.0.0", 9000).map(_ => println("Started"))
  }

  private def responseToString(): String = {
    mlpTrainer.loadModel()
    rfTrainer.loadModel()
    val publicDataset = client.getPublicDataset
    val dataFrameToClassify = PublicDatasetPayloadConverter.publicDStoDF(publicDataset.Payload)
    val normalized = DataPreprocessorFactory().normalizeToClassify(dataFrameToClassify)
    val mlpResult: Seq[(String, String)] = mlpTrainer.classify(normalized)
    val rfResult: Seq[(String, String)] = rfTrainer.classify(normalized)

    val response = Response(composeUsers(mlpResult ++ rfResult),
      success = publicDataset.Success.getOrElse(false),
      errors = publicDataset.Error.getOrElse(""))

    Response.encoder(response).toString()
  }

  private def composeUsers(classifications: Seq[(String, String)]): Seq[User] = {
    val groupedUsers: Map[String, List[(String, String)]] = classifications.toList.groupBy(_._1)
    groupedUsers.map(c => User(c._1, c._2.head._2, c._2(1)._2)).toList
  }
}
