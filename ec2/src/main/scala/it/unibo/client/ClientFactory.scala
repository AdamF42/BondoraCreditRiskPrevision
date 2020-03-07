package it.unibo.client

import it.unibo.client.environment.Environment

object ClientFactory {

  def apply(env: Environment): Client = {
    new BondoraApiClient(env)
  }

}
