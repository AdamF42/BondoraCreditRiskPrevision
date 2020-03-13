package it.unibo.client.environment

object EnvironmentFactory {

  val bondoraLocal = 0
  val bondora = 1

  def apply(env: Int): Environment = {
    env match {
      case `bondoraLocal` => Environment("", "http://localhost:8000")
      case `bondora` => Environment("", "https://api.bondora.com")
      case _ => throw new NoSuchElementException
    }
  }

}
