package Core.DataPreprocessor

object DataPreprocessor {

  val MLP = 0

  def apply(processorType: Int): BaseDataPreprocessor = {
    processorType match {
      case MLP => new MLPDataPreprocessor()
      case _ => new MLPDataPreprocessor()
    }
  }

}
