package it.unibo.filesys

object FileHandlerFactory {

  val df = 0
  val model = 1

  def apply(handlerType: Int): BaseFileHandler =
    handlerType match {
      case 0 => new DfFileHandler
      case 1 => new ModelFileHandler
    }
}
