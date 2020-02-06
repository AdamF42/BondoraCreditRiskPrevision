import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature.StringIndexer
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

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

    val endedLoans: Dataset[Row] = df.select(features.head, features.tail: _*)
      .where(df.col("ContractEndDate").isNotNull)

    val dfChangeType = endedLoans
      .withColumn("Age", endedLoans.col("Age").cast("int"))
      .withColumn("AppliedAmount", endedLoans.col("AppliedAmount").cast("double"))
      .withColumn("Interest", endedLoans.col("Interest").cast("double"))
      .withColumn("LoanDuration", endedLoans.col("LoanDuration").cast("int"))
      .withColumn("UseOfLoan", endedLoans.col("UseOfLoan").cast("int"))
      .withColumn("MaritalStatus", endedLoans.col("MaritalStatus").cast("int"))
      .withColumn("EmploymentStatus", endedLoans.col("EmploymentStatus").cast("int"))
      .withColumn("IncomeTotal", endedLoans.col("IncomeTotal").cast("double"))

    val indexers = dfChangeType.select("NewCreditCustomer", "Country", "Status").columns.map { colName =>
      new StringIndexer().setInputCol(colName).setOutputCol(colName + "Index")
    }

    val nomalized: DataFrame = new Pipeline()
      .setStages(indexers)
      .fit(dfChangeType)
      .transform(dfChangeType)

    nomalized.show()

  }

}
