package Core.DataPreprocessor

import org.apache.spark.sql.functions.{current_date, lit}
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

trait BaseDataPreprocessor {

  val features = Seq("Age",
    "AppliedAmount",
    "Interest",
    "LoanDuration",
    "UseOfLoan",
    "MaritalStatus",
    "EmploymentStatus",
    "IncomeTotal",
    "NewCreditCustomer",
    "Country",
    "Status")

  def readFile(sparkSession: SparkSession, filePath: String): DataFrame =
    sparkSession.read.format("csv")
      .option("header", value = true)
      .load(filePath)

  def filterEndedLoans(df: DataFrame): Dataset[Row] = {
    df.select(features.head, features.tail: _*)
      .where(df.col("ContractEndDate").lt(lit(current_date())))
  }

  def normalize(df: DataFrame): DataFrame

}
