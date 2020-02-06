

import org.apache.spark.sql.SparkSession

object BondoraCreditDefault {

  def setupLogging(): Unit = {
    import org.apache.log4j.{Level, Logger}
    val rootLogger = Logger.getRootLogger
    rootLogger.setLevel(Level.ERROR)
  }

  def main(args: Array[String]) {

    val spark = SparkSession
      .builder
      .appName("BondoraCreditRiskPrevision")
      .master("local[*]")
      .getOrCreate()

    //import spark.implicits._


    setupLogging()

    val df = spark.read.format("csv")
      .option("header", value = true)
      .load("../LoanData.csv")

    val endedLoans = df.select("Age",
      "NewCreditCustomer",
      "Country",
      "AppliedAmount",
      "Interest",
      "LoanDuration",
      "UseOfLoan",
      "MaritalStatus",
      "EmploymentStatus",
      "IncomeTotal",
      "Status")
      .where(df.col("ContractEndDate").isNotNull)

  }
}
