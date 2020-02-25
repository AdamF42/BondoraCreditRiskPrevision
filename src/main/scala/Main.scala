import Core.Assembler.AssemblerFactory
import Core.Classifier.ClassifierFactory
import Core.Classifier.ClassifierFactory.{MLP, RF}
import Core.DataPreprocessor.DataPreprocessorFactory
import Core.Normalizer.NormalizerFactory
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.ml.PipelineModel
import org.apache.spark.sql.{DataFrame, SparkSession}


object Main {

  val NORMALIZED_DATAFRAME = "../normalized.csv"

  def main(args: Array[String]): Unit = {

    implicit val spark: SparkSession = setupSparkSession

    val normalized: DataFrame = getNormalizedDataFrame

    val splits = normalized.randomSplit(Array(0.6, 0.4), seed = 1234L)
    val train = splits(0)
    val test = splits(1)

    val mlpTrainer = ClassifierFactory(MLP)
    val rfTrainer = ClassifierFactory(RF)

    val normalizer = NormalizerFactory().getNormalizer(normalized.columns)
    val assembler = AssemblerFactory().getAssembler(normalized.columns)

    val mlpModel: PipelineModel = mlpTrainer.train(train, Array(assembler, normalizer))
    val mlpResult = mlpTrainer.evaluate(test, mlpModel)
    val rfModel = rfTrainer.train(train, Array(assembler, normalizer))
    val rfResult = rfTrainer.evaluate(test, rfModel)

    println("MLP : " + mlpResult)
    println("RF  : " + rfResult)

  }

  private def getFileSystem()(implicit spark: SparkSession): FileSystem = {
    FileSystem.get(spark.sparkContext.hadoopConfiguration)
  }

  private def getNormalizedDataFrame()(implicit spark: SparkSession): DataFrame = {
    val fs: FileSystem = getFileSystem
    val fileExists = fs.exists(new Path(NORMALIZED_DATAFRAME))
    if (fileExists) retrieveNormalizedDataFrame()
    else getDataFrame
  }

  private def retrieveNormalizedDataFrame()(implicit spark: SparkSession): DataFrame = {
    spark.read.format("csv")
      .option("header", value = true)
      .option("inferSchema", "true")
      .load(NORMALIZED_DATAFRAME)
  }

  private def getDataFrame()(implicit spark: SparkSession): DataFrame = {

    val preprocessor = DataPreprocessorFactory(spark)
    val df = preprocessor.readFile("LoanData.csv")

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