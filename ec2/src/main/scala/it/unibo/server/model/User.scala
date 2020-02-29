package it.unibo.server.model

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

case class User(userId: Int, label: String)

object User {
  implicit val encoder: Encoder[User] = deriveEncoder[User]
}

