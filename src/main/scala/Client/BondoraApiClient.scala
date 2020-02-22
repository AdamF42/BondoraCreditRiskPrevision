package Client

import Client.Response.DataSetModelBase
import org.json4s.native.Serialization
import sttp.client._
import sttp.client.json4s.asJson
import sttp.model.Uri


class BondoraApiClient(authToken: String) {


  private lazy val baseRequest = basicRequest.auth.bearer(authToken)
  private lazy val baseRestUri: Uri = uri"https://api.bondora.com"
  implicit val serialization: Serialization.type = org.json4s.native.Serialization


  def getPublicDataset: Either[ResponseError[Exception], DataSetModelBase] = {

    implicit val backend: SttpBackend[Identity, Nothing, NothingT] = HttpURLConnectionBackend()

    val url = baseRestUri.path("api", "v1", "publicdataset")

    val request = baseRequest
      .get(url)
      .response(asJson[DataSetModelBase])

    request.send().body
  }

}
