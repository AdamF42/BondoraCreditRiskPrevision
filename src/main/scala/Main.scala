import Core.Classifier.Classifier
import Core.DataPreprocessor.DataPreprocessor
import Core.DataPreprocessor.DataPreprocessor.MLP
import org.apache.spark.sql.SparkSession

object Main {

  def setupSparkSession: SparkSession =
    SparkSession
      .builder
      .appName("BondoraCreditRiskPrevision")
      .master("local[*]")
      .getOrCreate()

  def setupLogging(): Unit = {
    import org.apache.log4j.{Level, Logger}
    val rootLogger = Logger.getRootLogger
    rootLogger.setLevel(Level.ERROR)
  }

  def main(args: Array[String]) {

    val spark: SparkSession = setupSparkSession

    setupLogging()

    val preprocessor = DataPreprocessor(MLP)

    val df = preprocessor.readFile(spark, "../LoanData.csv")

    val normalized = preprocessor.normalize(preprocessor.filterEndedLoans(df))

    val splits = normalized.randomSplit(Array(0.6, 0.4), seed = 1234L)
    val train = splits(0)
    val test = splits(1)

    val mlpTrainer = Classifier(MLP)
    val model = mlpTrainer.train(train, preprocessor.features)
    val result = mlpTrainer.evaluate(test, model)

    println(result)
  }

}
