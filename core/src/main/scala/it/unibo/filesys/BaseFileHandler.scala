package it.unibo.filesys

import scala.sys.process._

trait BaseFileHandler {

  private val patterns3bucket = "s3://.*"

  def isS3Folder(basePath: String): Boolean =
    basePath.matches(patterns3bucket)

  def copyToS3(fileName: String, bucket: String): Int

  def copyFromS3(fileName: String, bucket: String): Int

  def createModelFolder(): Int = {
    Seq("rm", "-rf", "models").!
    Seq("mkdir", "models").!
  }

}
