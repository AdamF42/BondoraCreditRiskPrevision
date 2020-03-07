import java.net.URI

import it.unibo.S3Load
import it.unibo.assembler.AssemblerFactory
import it.unibo.classifier.ClassifierFactory
import it.unibo.classifier.ClassifierFactory.{MLP, RF}
import it.unibo.datapreprocessor.DataPreprocessorFactory
import it.unibo.normalizer.NormalizerFactory
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

object Main {

  val normalizedDataSetPath: String = "/normalized.csv"
  val originDataSetPath: String = "/LoanData.csv"

  def main(args: Array[String]): Unit = {

    implicit val spark: SparkSession = setupSparkSession

    val basePath: String = args.headOption getOrElse ".."

    val normalized: DataFrame = getNormalizedDataFrame(basePath)

    val splits = normalized.randomSplit(Array(0.6, 0.4), seed = 1234L)
    val train: Dataset[Row] = splits(0)
    val test = splits(1)

    val mlpTrainer = ClassifierFactory(MLP)
    val rfTrainer = ClassifierFactory(RF)

    val normalizer = NormalizerFactory().getNormalizer(normalized.columns)
    val assembler = AssemblerFactory().getAssembler(normalized.columns)

    mlpTrainer.train(train, Array(assembler, normalizer))
    rfTrainer.train(train, Array(assembler, normalizer))

    S3Load.createModelFolder()

    mlpTrainer.saveModel()
    rfTrainer.saveModel()

    S3Load.copyModelToS3("mlp", bucketName)
    S3Load.copyModelToS3("rf", bucketName)

    S3Load.copyModelFromS3("mlp", bucketName)
    S3Load.copyModelFromS3("rf", bucketName)

    mlpTrainer.loadModel()
    rfTrainer.loadModel()

    println("EVALUATING TRAINING OF MODELS")

    val mlpResult = mlpTrainer.evaluate(test)
    val rfResult = rfTrainer.evaluate(test)

    println("MLP : " + mlpResult)
    println("RF  : " + rfResult)

  }

  private def getNormalizedDataFrame(root: String)(implicit spark: SparkSession): DataFrame = {
    val conf = spark.sparkContext.hadoopConfiguration
    val fs = FileSystem.get(URI.create(root), conf)
    if (fs.exists(new Path(root + normalizedDataSetPath))) retrieveNormalizedDataFrame(root)
    else getDataFrame(root)
  }

  private def retrieveNormalizedDataFrame(root: String)(implicit spark: SparkSession): DataFrame =
    spark.read.format("csv")
      .option("header", value = true)
      .option("inferSchema", "true")
      .load(root + normalizedDataSetPath)

  private def getDataFrame(root: String)(implicit spark: SparkSession): DataFrame = {

    val preprocessor = DataPreprocessorFactory(spark)
    val df = preprocessor.readFile(root + originDataSetPath)

    preprocessor.normalizeToTrain(df)
  }

  def setupSparkSession: SparkSession = {

    val session = SparkSession
      .builder
      .appName("BondoraCreditRiskPrevision")
//      .master("local[*]")
      .getOrCreate()
    setupLogging()
    session
  }

  def setupLogging(): Unit = {
    val rootLogger = Logger.getRootLogger
    rootLogger.setLevel(Level.ERROR)
  }

}
