package it.unibo.core.assembler

object AssemblerFactory {

  def apply(): BaseCustomAssembler =
    new CustomAssembler()

}
