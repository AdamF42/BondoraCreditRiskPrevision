package it.unibo.classifier

object ClassifierFactory {

  val MLP = 0
  val RF = 1

  def apply(processorType: Int): BaseClassifier = {
    processorType match {
      case MLP => new MLPClassifier()
      case RF => new RFClassifier()
      case _ => new MLPClassifier()
    }
  }

}
