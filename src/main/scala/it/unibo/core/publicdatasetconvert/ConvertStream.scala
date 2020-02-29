package it.unibo.core.publicdatasetconvert

import it.unibo.client.model.{PublicDataset, PublicDatasetPayload}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.types.{StructField, StructType}

object ConvertStream {

  def readStream(publicData: PublicDataset)(implicit spark: SparkSession): DataFrame = {

    val payloads = publicData.Payload

    val mapsColumnValue: Seq[Map[String, Any]] = payloads.map(p => ccToMap(p).map(e => (e._1, e._2 match {
      case Some(i) => i
      case None => null
    })))

    spark.sqlContext.createDataFrame(getRDD(mapsColumnValue), getSchema(mapsColumnValue))
  }

  private def getRDD(mapsColumnValue: Seq[Map[String, Any]])(implicit spark: SparkSession) = {
    val rows = mapsColumnValue.map(m => Row(m.values.toSeq: _*))
    spark.sparkContext.parallelize(rows)
  }

  private def getSchema(mapsColumnValue: Seq[Map[String, Any]]) = {
    StructType(mapsColumnValue.head.toList.map(
      m => StructField(m._1, PublicDatasetPayload.dataTypes(m._1), nullable = true)
    ))
  }

  private def ccToMap(cc: Product): Map[String, Any] = {
    val values = cc.productIterator
    cc.getClass.getDeclaredFields.map(_.getName -> values.next).toMap
  }

}
