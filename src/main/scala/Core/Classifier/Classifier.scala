package Core.Classifier

object Classifier {

  val MLP = 0

  def apply(processorType: Int): BaseClassifier = {
    processorType match {
      case MLP => new MLPClassifier()
      case _ => new MLPClassifier()
    }
  }

}
