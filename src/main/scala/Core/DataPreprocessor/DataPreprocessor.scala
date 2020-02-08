package Core.DataPreprocessor

object DataPreprocessor {

  val BASE = 0

  def apply(processorType: Int): BaseDataPreprocessor = {
    processorType match {
      case BASE => new MLPDataPreprocessor()
      case _ => new MLPDataPreprocessor()
    }
  }

}
