package Core.DataPreprocessor

import org.apache.spark.sql.DataFrame

trait BaseDataPreprocessor {

  def readFile(filePath: String): DataFrame

  def normalize(df: DataFrame): DataFrame

}
