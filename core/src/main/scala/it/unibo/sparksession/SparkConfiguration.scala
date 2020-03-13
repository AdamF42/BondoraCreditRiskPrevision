package it.unibo.sparksession

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession

class SparkConfiguration extends Configuration {

  def getOrCreateSession: SparkSession = {

    val session = SparkSession
      .builder
      .appName("BondoraCreditRiskPrevision")
      .master("local[*]")
      .getOrCreate()
    setupLogging()
    session
  }

  private def setupLogging(): Unit = {
    val rootLogger = Logger.getRootLogger
    rootLogger.setLevel(Level.ERROR)
  }
}
