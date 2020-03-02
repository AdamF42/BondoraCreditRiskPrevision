package it.unibo.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.{get, _}
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import it.unibo.server.model.{Response, User}


object Server extends App {

  def responseToString(): String = {
    val user = User(1, "Paid")
    val response = Response(Seq(user), success = true, "")
    Response.encoder(response).toString()
  }

  val serverRoutes: Route =
    pathPrefix("data") {
      get {
        complete(responseToString())
      }
    }

  val host = "0.0.0.0"
  val port = 9000

  implicit val actorSystem: ActorSystem = ActorSystem("sttp-pres")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  import actorSystem.dispatcher

  Http().bindAndHandle(serverRoutes, host, port).map(_ => println("Started"))

}