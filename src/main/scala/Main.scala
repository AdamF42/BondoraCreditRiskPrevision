import Core.Assembler.AssemblerFactory
import Core.Classifier.ClassifierFactory
import Core.Classifier.ClassifierFactory.{MLP, RF}
import Core.DataPreprocessor.DataPreprocessorFactory
import Core.Normalizer.NormalizerFactory
import org.apache.spark.sql.{DataFrame, SparkSession}

object Main {

  def main(args: Array[String]) {

    val normalized: DataFrame = getDataFrame("../LoanData.csv")

    val splits = normalized.randomSplit(Array(0.6, 0.4), seed = 1234L)
    val train = splits(0)
    val test = splits(1)

    val mlpTrainer = ClassifierFactory(MLP)
    val rfTrainer = ClassifierFactory(RF)

    val normalizer = NormalizerFactory().getNormalizer(normalized.columns)
    val assembler = AssemblerFactory().getAssembler(normalized.columns)

    val mlpModel = mlpTrainer.train(train, Array(assembler, normalizer))
    val mlpResult = mlpTrainer.evaluate(test, mlpModel)
    val rfModel = rfTrainer.train(train, Array(assembler, normalizer))
    val rfResult = rfTrainer.evaluate(test, rfModel)

    println("MLP : " + mlpResult)
    println("RF  : " + rfResult)
  }

  private def getDataFrame(filePath: String) = {

    val spark: SparkSession = setupSparkSession

    setupLogging()

    val preprocessor = DataPreprocessorFactory(spark)

    val df = preprocessor.readFile(filePath)

    preprocessor.normalize(df)

  }

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

}
