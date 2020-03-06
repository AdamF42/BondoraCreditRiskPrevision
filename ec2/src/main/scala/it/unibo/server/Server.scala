package it.unibo.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import it.unibo.classifier.ClassifierFactory
import it.unibo.classifier.ClassifierFactory.MLP
import it.unibo.classifier.ClassifierFactory.RF
import it.unibo.server.model.{Response, User}

object Server extends App {
  val serverRoutes: Route =
    pathPrefix("data") {
      get {
        complete(responseToString())
      }
    }
  val host = "0.0.0.0"
  val port = 9000

  def responseToString(): String = {
    val mlpTrainer = ClassifierFactory(MLP)
    val rfTrainer = ClassifierFactory(RF)
    mlpTrainer.loadModel()
    rfTrainer.loadModel()
    val user = User(1, "Paid")
    val response = Response(Seq(user), success = true, "")
    Response.encoder(response).toString()
  }

  implicit val actorSystem: ActorSystem = ActorSystem("sttp-pres")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  import actorSystem.dispatcher

  Http().bindAndHandle(serverRoutes, host, port).map(_ => println("Started"))

}