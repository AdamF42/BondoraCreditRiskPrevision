package it.unibo.server.model

import io.circe.Json
import io.circe.parser._
import io.circe.syntax._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ResponseTest extends AnyWordSpec with Matchers {

  "Response.encoder" should {
    "encode correctly" in {

      val expected = parse(
        """{
          |  "users" : [
          |    {
          |      "userId" : "BO6KKK763",
          |      "mlpLabel" : "Late",
          |      "rfLabel" : "Late"
          |    }
          |  ],
          |  "success" : true,
          |  "errors" : ""
          |}""".stripMargin)
        .right.getOrElse(Json.Null)

      val actual = Response(Seq(User("BO6KKK763", "Late", "Late")), success = true, "").asJson

      actual shouldBe expected

    }
  }

}
