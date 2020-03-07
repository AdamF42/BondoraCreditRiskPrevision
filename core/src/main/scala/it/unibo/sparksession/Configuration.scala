package it.unibo.sparksession

import org.apache.spark.sql.SparkSession

trait Configuration {

  def getOrCreateSession: SparkSession
}
