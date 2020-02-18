package Core.Assembler

object Assembler {

  val BASE = 0

  def apply(processorType: Int): BaseCustomAssembler = {
    processorType match {
      case BASE => new CustomAssembler()
      case _ => new CustomAssembler()
    }
  }

}
