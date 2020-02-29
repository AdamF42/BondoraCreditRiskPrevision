import Main.setupSparkSession
import it.unibo.client.BondoraApiClient
import it.unibo.client.model.PublicDatasetPayload
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

object TestResponse {

  def main(args: Array[String]): Unit = {

    implicit val spark: SparkSession = setupSparkSession

    val pippo = new BondoraApiClient("")
    val body = pippo.getPublicDataset

    body match {
      case Right(x) =>
        val pluto: Seq[PublicDatasetPayload] = x.Payload
        val df: DataFrame = readStream(pluto)
      case Left(x) => x.printStackTrace()
    }
  }

  def ccToMap(cc: Product): Map[String, Any] = {
    val values = cc.productIterator
    cc.getClass.getDeclaredFields.map(_.getName -> values.next).toMap
  }

  def readStream(payloads: Seq[PublicDatasetPayload])(implicit spark: SparkSession): DataFrame = {

    val res: Seq[Map[String, Any]] = payloads.map(p => ccToMap(p).map(e => (e._1, e._2 match {
      case Some(i) => i
      case None => null
    })))

    val schema = StructType(res.head.toList.map(
      m => StructField(m._1, PublicDatasetPayload.dataTypes(m._1), nullable = true)
    ))

    val rows = res.map(m => Row(m.values.toSeq: _*))

    val rdd = spark.sparkContext.parallelize(rows)


    spark.sqlContext.createDataFrame(rdd, schema)
  }

}