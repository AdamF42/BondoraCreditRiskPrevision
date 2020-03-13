package it.unibo.server.model

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

case class Response(users: Seq[User], success: Boolean, errors: String)

object Response {
  implicit val encoder: Encoder[Response] = deriveEncoder[Response]
}