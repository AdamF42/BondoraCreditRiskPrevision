package it.unibo.datapreprocessor

import org.apache.spark.sql.SparkSession

object DataPreprocessorFactory {

  def apply(session: SparkSession): BaseDataPreprocessor =
    new DataPreprocessor(session)

}
