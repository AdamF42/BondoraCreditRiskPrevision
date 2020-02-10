package DataPreprocessor

import Core.DataPreprocessor.BaseDataPreprocessor
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature.StringIndexer
import org.apache.spark.sql.{DataFrame, SparkSession}

class MLPDataPreprocessor(sparkSession: SparkSession, pipeline: Pipeline) extends BaseDataPreprocessor(sparkSession: SparkSession) {

  override def normalize(df: DataFrame): DataFrame = {

    val dfChangeType = df
      .withColumn("Age", df.col("Age").cast("int"))
      .withColumn("AppliedAmount", df.col("AppliedAmount").cast("double"))
      .withColumn("Interest", df.col("Interest").cast("double"))
      .withColumn("LoanDuration", df.col("LoanDuration").cast("int"))
      .withColumn("UseOfLoan", df.col("UseOfLoan").cast("int"))
      .withColumn("MaritalStatus", df.col("MaritalStatus").cast("int"))
      .withColumn("EmploymentStatus", df.col("EmploymentStatus").cast("int"))
      .withColumn("IncomeTotal", df.col("IncomeTotal").cast("double"))

    val indexers = dfChangeType.select("NewCreditCustomer", "Country", "Status").columns.map { colName =>
      new StringIndexer().setInputCol(colName).setOutputCol(colName + "Index")
    }

    pipeline
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
