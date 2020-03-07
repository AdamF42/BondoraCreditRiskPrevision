package it.unibo.client.environment

object EnvironmentFactory {

  val BONDORA_LOCAL = 0
  val BONDORA = 1

  def apply(env: Int): Environment = {
    env match {
      case BONDORA_LOCAL => Environment("", s"http://localhost:8000")
      case BONDORA => Environment("", "https://api.bondora.com")
      case _ => throw new NoSuchElementException
    }
  }

}
