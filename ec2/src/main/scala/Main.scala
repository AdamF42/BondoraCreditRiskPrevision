import it.unibo.client.environment.{Environment, EnvironmentFactory}
import it.unibo.client.{Client, ClientFactory}
import it.unibo.server.Server
import it.unibo.sparksession.SparkConfiguration


object Main {

  def main(args: Array[String]): Unit = {

    val basePath: String = args.headOption getOrElse ".."

    val env: Environment = EnvironmentFactory(EnvironmentFactory.BONDORA_LOCAL)
    val client: Client = ClientFactory(env)

    implicit val sparkConfiguration: SparkConfiguration = new SparkConfiguration()

    val server = new Server(client, basePath)

    server.start
  }

}
