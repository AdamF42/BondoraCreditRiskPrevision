package it.unibo.client

import it.unibo.client.model.PublicDataset

trait Client {
  def getPublicDataset: PublicDataset
}
