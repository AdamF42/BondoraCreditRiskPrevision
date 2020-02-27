package Client

import Client.Response.DataSetModelBase
import org.json4s.native.Serialization
import sttp.client._
import sttp.client.json4s.asJson
import sttp.model.Uri


class BondoraApiClient(authToken: String) {


  private lazy val baseRequest = basicRequest.auth.bearer(authToken)
//  private lazy val baseRestUri: Uri = uri"https://api.bondora.com"
  private lazy val baseRestUri: Uri = uri"http://localhost:8000"


  def getPublicDataset: Either[ResponseError[Exception], DataSetModelBase] = {

    implicit val backend: SttpBackend[Identity, Nothing, NothingT] = HttpURLConnectionBackend()

    implicit val serializer: Serialization.type = Serialization

    val url = baseRestUri.path("api", "v1", "publicdataset")
      .param("request.loanDateFrom", java.time.LocalDate.now.toString)
    
    val request = baseRequest
      .get(url)
      .response(asJson[DataSetModelBase])

    request.send().body
  }

}
