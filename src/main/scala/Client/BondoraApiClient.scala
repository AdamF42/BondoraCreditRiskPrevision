package Client

import io.circe
import sttp.client._
import sttp.client.circe._

class BondoraApiClient(authToken: String) {

  private lazy val baseRequest = basicRequest.auth.bearer(authToken)
  private lazy val baseRestUri: Uri = uri"https://api.bondora.com"

  def getPublicDataset: Either[ResponseError[circe.Error], PublicDataset] = {

    implicit val backend: SttpBackend[Identity, Nothing] = HttpURLConnectionBackend()

    val url = baseRestUri.path("api", "v1", "publicdataset")
      .param("request.loanDateFrom", java.time.LocalDate.now.toString)

    val request = baseRequest
      .get(url)
      .response(asJson[PublicDataset])

    request.send().body
  }

}
