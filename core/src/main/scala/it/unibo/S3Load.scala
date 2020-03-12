package it.unibo

import scala.sys.process._

object S3Load {

  private val patterns3bucket = "s3://.*"

  def isS3Folder(basePath: String): Boolean =
    basePath.matches(patterns3bucket)

  def copyModelFromS3(name: String, bucket: String): Int =
    Seq("aws", "s3", "cp", s"${bucket}/models/${name}.zip", s"./models/").!

  def copyModelToS3(name: String, bucket: String): Int =
    Seq("aws", "s3", "cp", s"./models/${name}.zip", s"${bucket}/models/").!

  def createModelFolder(): Int = {
    Seq("rm", "-rf", "models").!
    Seq("mkdir", "models").!
  }


}