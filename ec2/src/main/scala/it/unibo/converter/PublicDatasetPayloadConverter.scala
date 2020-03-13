package it.unibo.converter

import it.unibo.client.model.PublicDatasetPayload
import it.unibo.sparksession.Configuration
import org.apache.spark.sql.types.{StructField, StructType}
import org.apache.spark.sql.{DataFrame, Row}

object PublicDatasetPayloadConverter {

  def publicDStoDF(payloads: Seq[PublicDatasetPayload])(implicit sparkConfiguration: Configuration): DataFrame = {

    val mapsColumnValue: Seq[Map[String, Any]] = payloads.map(p => ccToMap(p).map(e => (e._1, e._2 match {
      case Some(i) => i
      case None => null
    })))

    sparkConfiguration.getOrCreateSession.sqlContext.createDataFrame(getRDD(mapsColumnValue), getSchema(mapsColumnValue))
  }

  private def getRDD(mapsColumnValue: Seq[Map[String, Any]])(implicit sparkConfiguration: Configuration) = {
    val rows = mapsColumnValue.map(m => Row(m.values.toSeq: _*))
    sparkConfiguration.getOrCreateSession.sparkContext.parallelize(rows)
  }

  private def getSchema(mapsColumnValue: Seq[Map[String, Any]]) = {

    val firstElement: Map[String, Any] = mapsColumnValue.headOption getOrElse Map.empty[String, Any]

    StructType(firstElement.toList.map(
      m => StructField(m._1, PublicDatasetPayload.dataTypes(m._1), nullable = true)
    ))

  }

  private def ccToMap(cc: Product): Map[String, Any] = {
    val values = cc.productIterator
    cc.getClass.getDeclaredFields.map(_.getName -> values.next).toMap
  }

}
