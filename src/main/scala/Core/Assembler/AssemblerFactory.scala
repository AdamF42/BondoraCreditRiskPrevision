package Core.Assembler

object AssemblerFactory {

  def apply(): BaseCustomAssembler =
    new CustomAssembler()

}
