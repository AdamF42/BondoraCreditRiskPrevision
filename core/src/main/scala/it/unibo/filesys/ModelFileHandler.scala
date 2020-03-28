package it.unibo.filesys

import scala.sys.process._

class ModelFileHandler extends BaseFileHandler {

  override def copyToS3(fileName: String, bucket: String): Int =
    Seq("aws", "s3", "cp", s"./models/${fileName}.zip", s"${bucket}/models/").!

  override def copyFromS3(fileName: String, bucket: String): Int =
    Seq("aws", "s3", "cp", s"${bucket}/models/${fileName}.zip", s"./models/").!

}
