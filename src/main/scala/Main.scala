import it.unibo.core.assembler.AssemblerFactory
import it.unibo.core.classifier.ClassifierFactory
import it.unibo.core.classifier.ClassifierFactory.{MLP, RF}
import it.unibo.core.datapreprocessor.DataPreprocessorFactory
import it.unibo.core.normalizer.NormalizerFactory
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}


object Main {

  val normalizedDataSetPath: String = "../normalized.csv"
  val baseDir: String = System.getProperty("user.dir") + "/models"
  val mlpPath: String = "/mlp.zip"
  val rfUri: String = "/rf.zip"

  def main(args: Array[String]): Unit = {

    implicit val spark: SparkSession = setupSparkSession

    val normalized: DataFrame = getNormalizedDataFrame

    val splits = normalized.randomSplit(Array(0.6, 0.4), seed = 1234L)
    val train: Dataset[Row] = splits(0)
    val test = splits(1)

    val mlpTrainer = ClassifierFactory(MLP)
    val rfTrainer = ClassifierFactory(RF)

    val normalizer = NormalizerFactory().getNormalizer(normalized.columns)
    val assembler = AssemblerFactory().getAssembler(normalized.columns)

    mlpTrainer.train(train, Array(assembler, normalizer))
    rfTrainer.train(train, Array(assembler, normalizer))

    mlpTrainer.saveModel()
    rfTrainer.saveModel()

    mlpTrainer.loadModel()
    mlpTrainer.loadModel()

    val mlpResult = mlpTrainer.evaluate(test)
    val rfResult = mlpTrainer.evaluate(test)

    println("MLP : " + mlpResult)
    println("RF  : " + rfResult)

  }

  private def getNormalizedDataFrame()(implicit spark: SparkSession): DataFrame = {
    val fs: FileSystem = FileSystem.get(spark.sparkContext.hadoopConfiguration)
    val fileExists = fs.exists(new Path(normalizedDataSetPath))
    if (fileExists) retrieveNormalizedDataFrame()
    else getDataFrame
  }

  private def retrieveNormalizedDataFrame()(implicit spark: SparkSession): DataFrame =
    spark.read.format("csv")
      .option("header", value = true)
      .option("inferSchema", "true")
      .load(normalizedDataSetPath)

  private def getDataFrame()(implicit spark: SparkSession): DataFrame = {

  private def getDataFrame()(implicit spark: SparkSession): DataFrame = {

    val preprocessor = DataPreprocessorFactory(spark)
    val df = preprocessor.readFile("../LoanData.csv")

    preprocessor.normalize(df)
  }

  def setupSparkSession: SparkSession = {

    val session = SparkSession
      .builder
      .appName("BondoraCreditRiskPrevision")
      .master("local[*]")
      .getOrCreate()
    setupLogging()
    session
  }

  def setupLogging(): Unit = {
    import org.apache.log4j.{Level, Logger}
    val rootLogger = Logger.getRootLogger
    rootLogger.setLevel(Level.ERROR)
  }

}