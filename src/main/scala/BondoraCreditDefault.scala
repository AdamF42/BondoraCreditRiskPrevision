import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.MultilayerPerceptronClassifier
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.{StringIndexer, VectorAssembler}
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

object BondoraCreditDefault {

  val features = Seq("Age",
    "AppliedAmount",
    "Interest",
    "LoanDuration",
    "UseOfLoan",
    "MaritalStatus",
    "EmploymentStatus",
    "IncomeTotal")

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

    val endedLoans: Dataset[Row] = df.select("Status", features :+ "NewCreditCustomer" :+ "Country": _*)
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
      new StringIndexer().setInputCol(colName).setOutputCol(if (colName.equals("Status")) "label" else colName + "Index")
    }

    val normalized: DataFrame = new Pipeline()
      .setStages(indexers)
      .fit(dfChangeType)
      .transform(dfChangeType)
      .select("label", features :+ "NewCreditCustomerIndex" :+ "CountryIndex": _*)

    // Split the data into train and test
    val splits = normalized.randomSplit(Array(0.6, 0.4), seed = 1234L)
    val train = splits(0)
    val test = splits(1)

    // specify layers for the neural network:
    val layers = Array[Int](11, 6, 3)

    //creating features column
    val assembler = new VectorAssembler()
      .setHandleInvalid("skip")
      .setInputCols(Array(features :+ "label" :+ "NewCreditCustomerIndex" :+ "CountryIndex": _*))
      .setOutputCol("features")

    // create the trainer and set its parameters
    val trainer = new MultilayerPerceptronClassifier()
      .setLayers(layers)
      .setBlockSize(64)
      .setSeed(1234L)
      .setFeaturesCol("features") // setting features column
      .setMaxIter(380)

    //creating pipeline
    val pipeline = new Pipeline().setStages(Array(assembler, trainer))

    // train the model
    val model = pipeline.fit(train)

    // compute accuracy on the test set
    val result = model.transform(test)
    val predictionAndLabels = result.select("prediction", "label")
    val evaluator = new MulticlassClassificationEvaluator()
      .setMetricName("accuracy")

    println(s"Test set accuracy = ${evaluator.evaluate(predictionAndLabels)}")

  }

}
