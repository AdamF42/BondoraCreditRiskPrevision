package Core.Classifier

import Core.Assembler.Assembler

object Classifier {

  val MLP = 0
  val RF = 1

  def apply(processorType: Int): BaseClassifier = {
    processorType match {
      case MLP => new MLPClassifier(Assembler(Assembler.BASE))
      case RF => new RFClassifier(Assembler(Assembler.BASE))
      case _ => new MLPClassifier(Assembler(Assembler.BASE))
    }
  }

}
