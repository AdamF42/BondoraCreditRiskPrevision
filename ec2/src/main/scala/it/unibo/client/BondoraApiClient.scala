package it.unibo.client

import it.unibo.client.environment.Environment
import it.unibo.client.model.PublicDataset
import sttp.client._
import sttp.client.circe._
import sun.reflect.generics.reflectiveObjects.NotImplementedException

class BondoraApiClient(env: Environment) extends Client {

  private lazy val baseRequest = basicRequest.auth.bearer(env.outhToken)
  private lazy val baseRestUri: Uri = uri"${env.url}"

  def getPublicDataset: PublicDataset = {

    implicit val backend: SttpBackend[Identity, Nothing] = HttpURLConnectionBackend()

    val url = baseRestUri.path("api", "v1", "publicdataset")
      .param("request.loanDateFrom", java.time.LocalDate.now.toString)

    val request = baseRequest
      .get(url)
      .response(asJson[PublicDataset])

    request.send().body.right.getOrElse(throw new NotImplementedException)
  }

}