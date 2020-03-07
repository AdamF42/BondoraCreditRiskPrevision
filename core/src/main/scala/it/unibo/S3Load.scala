package it.unibo

import scala.sys.process._

object S3Load {

  def copyModelFromS3(name: String, bucket: String) =
    Seq("aws", "s3", "cp", s"${bucket}/models/${name}.zip", s"./models/").!

  def copyModelToS3(name: String, bucket: String) =
    Seq("aws", "s3", "cp", s"./models/${name}.zip", s"${bucket}/models/").!

  def createModelFolder() = {
    Seq("rm", "-rf", "models").!
    Seq("mkdir", "models").!
  }


}