package it.unibo

import scala.sys.process._

object S3Load {

  def copyFromS3(name: String, bucket:String) =
    Seq("aws", "s3", "cp", s"s3://${bucket}/models/${name}", s"./models/").!


  def copyToS3(name: String, bucket:String) =
    Seq("aws", "s3", "cp", s"./models/${name}", s"s3://${bucket}/models/").!

}
