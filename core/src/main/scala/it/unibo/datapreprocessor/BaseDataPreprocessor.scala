package it.unibo.datapreprocessor

import org.apache.spark.sql.DataFrame

trait BaseDataPreprocessor {

  def readFile(filePath: String): DataFrame

  def normalizeToTrain(df: DataFrame): DataFrame

  def normalizeToClassify(df: DataFrame): DataFrame

}
