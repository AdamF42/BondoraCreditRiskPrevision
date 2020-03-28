package it.unibo.filesys

import scala.sys.process._

class DfFileHandler extends BaseFileHandler {

  override def copyToS3(fileName: String, bucket: String): Int = {
    Seq("hdfs", "dfs", "-copyToLocal", s"./${fileName}/", ".").!
    Seq("aws", "s3", "cp", "--recursive", s"./${fileName}/", s"${bucket}/${fileName}/").!
  }

  override def copyFromS3(fileName: String, bucket: String): Int =
    Seq("aws", "s3", "cp", "--recursive", s"${bucket}/${fileName}/", s"./${fileName}/").!

}
