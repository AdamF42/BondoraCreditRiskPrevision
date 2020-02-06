import org.apache.spark.sql.SparkSession

object BondoraCreditDefault {

  val features = Seq("Age",
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

    setupLogging()

    val df = spark.read.format("csv")
      .option("header", value = true)
      .load("../LoanData.csv")

    val endedLoans = df.select(features.head,features.tail:_*)
      .where(df.col("ContractEndDate").isNotNull)

  }

}
