package Core.DataPreprocessor

import org.apache.spark.ml
import org.apache.spark.ml.feature.StringIndexer
import org.apache.spark.sql.DataFrame

private class MLPDataPreprocessor extends BaseDataPreprocessor {

  private def castDataFrame(df: DataFrame): DataFrame =
    df.withColumn("Age", df.col("Age").cast("int"))
      .withColumn("AppliedAmount", df.col("AppliedAmount").cast("double"))
      .withColumn("Interest", df.col("Interest").cast("double"))
      .withColumn("LoanDuration", df.col("LoanDuration").cast("int"))
      .withColumn("UseOfLoan", df.col("UseOfLoan").cast("int"))
      .withColumn("MaritalStatus", df.col("MaritalStatus").cast("int"))
      .withColumn("EmploymentStatus", df.col("EmploymentStatus").cast("int"))
      .withColumn("IncomeTotal", df.col("IncomeTotal").cast("double"))

  private def createIndex(dataFrame: DataFrame) =
    dataFrame.select("NewCreditCustomer", "Country", "Status").columns.map { colName =>
      new StringIndexer().setInputCol(colName).setOutputCol(colName + "Index")
    }

  override def normalize(df: DataFrame): DataFrame = {

    val dfChangeType: DataFrame = castDataFrame(df)

    val indexers = createIndex(dfChangeType)

    new ml.Pipeline()
      .setStages(indexers)
      .fit(dfChangeType)
      .transform(dfChangeType)
      .drop("NewCreditCustomer")
      .drop("Country")
      .drop("Status")
      .withColumnRenamed("NewCreditCustomerIndex", "NewCreditCustomer")
      .withColumnRenamed("CountryIndex", "Country")
      .withColumnRenamed("StatusIndex", "Status")
  }
}