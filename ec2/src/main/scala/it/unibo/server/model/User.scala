package it.unibo.server.model

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

case class User(userId: String, mlpLabel: String, rfLabel: String)

object User {
  implicit val encoder: Encoder[User] = deriveEncoder[User]
}

