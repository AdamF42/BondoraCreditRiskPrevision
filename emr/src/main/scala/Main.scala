import it.unibo.assembler.AssemblerFactory
import it.unibo.classifier.ClassifierFactory
import it.unibo.classifier.ClassifierFactory.{MLP, RF}
import it.unibo.datapreprocessor.DataPreprocessorFactory
import it.unibo.normalizer.NormalizerFactory
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}
import sttp.client.ResponseError

object Main {

  val normalizedDataSetPath: String = "../normalized.csv"

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
    rfTrainer.loadModel()

    val mlpResult = mlpTrainer.evaluate(test)
    val rfResult = rfTrainer.evaluate(test)

    println("MLP : " + mlpResult)
    println("RF  : " + rfResult)

    val bondora: BondoraApiClient = new BondoraApiClient("")
    val publicdataset: Either[ResponseError[circe.Error], PublicDataset] = bondora.getPublicDataset
    publicdataset match {
      case Right(x) => val df = PublicDatasetPayloadConverter.publicDStoDF(x.Payload)

        val resultMLPClassify = mlpTrainer.classify(df)
        println("MLP Classify (user,prediction) : " + resultMLPClassify)

        val resultRFClassify = rfTrainer.classify(df)
        println("RF Classify (user,prediction) : " + resultRFClassify)

      case Left(x) => println(x)
    }

  }

  private def getNormalizedDataFrame()(implicit spark: SparkSession): DataFrame = {
    val fs: FileSystem = FileSystem.get(spark.sparkContext.hadoopConfiguration)
    if (fs.exists(new Path(normalizedDataSetPath))) retrieveNormalizedDataFrame()
    else getDataFrame
  }

  private def retrieveNormalizedDataFrame()(implicit spark: SparkSession): DataFrame =
    spark.read.format("csv")
      .option("header", value = true)
      .option("inferSchema", "true")
      .load(normalizedDataSetPath)

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
    val rootLogger = Logger.getRootLogger
    rootLogger.setLevel(Level.ERROR)
  }

}