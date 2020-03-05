package it.unibo.assembler

object AssemblerFactory {

  def apply(): BaseCustomAssembler =
    new CustomAssembler()

}
