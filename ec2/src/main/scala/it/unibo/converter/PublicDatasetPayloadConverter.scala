package it.unibo.converter

import it.unibo.client.model.PublicDatasetPayload
import it.unibo.sparksession.Configuration
import org.apache.spark.sql.DataFrame


object PublicDatasetPayloadConverter {

  def publicDStoDF(payloads: Seq[PublicDatasetPayload])(implicit sparkConfiguration: Configuration): DataFrame = {

    val payloadRDD = sparkConfiguration.getOrCreateSession.sparkContext.parallelize(payloads)
    sparkConfiguration.getOrCreateSession.sqlContext.createDataFrame(payloadRDD)

  }
}
