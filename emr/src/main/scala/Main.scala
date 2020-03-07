import java.net.URI

import it.unibo.S3Load
import it.unibo.assembler.AssemblerFactory
import it.unibo.classifier.ClassifierFactory
import it.unibo.classifier.ClassifierFactory.{MLP, RF}
import it.unibo.datapreprocessor.DataPreprocessorFactory
import it.unibo.normalizer.NormalizerFactory
import it.unibo.sparksession.{Configuration, SparkConfiguration}
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.sql.{DataFrame, Dataset, Row}

object Main {

  val normalizedDataSetPath: String = "/normalized.csv"
  val originDataSetPath: String = "/LoanData.csv"
  private val patterns3bucket = "s3://.*"

  def main(args: Array[String]): Unit = {

    implicit val sparkConfiguration: SparkConfiguration = new SparkConfiguration()

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

    if (basePath.matches(patterns3bucket)) {

      S3Load.copyModelToS3("mlp", basePath)
      S3Load.copyModelToS3("rf", basePath)

      S3Load.copyModelFromS3("mlp", basePath)
      S3Load.copyModelFromS3("rf", basePath)

    }

    mlpTrainer.loadModel()
    rfTrainer.loadModel()

    val mlpResult = mlpTrainer.evaluate(test)
    val rfResult = rfTrainer.evaluate(test)

    println("MLP : " + mlpResult)
    println("RF  : " + rfResult)

  }

  private def getNormalizedDataFrame(root: String)(implicit sparkConfiguration: Configuration): DataFrame = {
    val conf = sparkConfiguration.getOrCreateSession.sparkContext.hadoopConfiguration
    val fs = FileSystem.get(URI.create(root), conf)
    if (fs.exists(new Path(root + normalizedDataSetPath))) retrieveNormalizedDataFrame(root)
    else getDataFrame(root)
  }

  private def retrieveNormalizedDataFrame(root: String)(implicit sparkConfiguration: Configuration): DataFrame =
    sparkConfiguration.getOrCreateSession.read.format("csv")
      .option("header", value = true)
      .option("inferSchema", "true")
      .load(root + normalizedDataSetPath)

  private def getDataFrame(root: String)(implicit sparkConfiguration: Configuration): DataFrame = {

    val preprocessor = DataPreprocessorFactory()
    val df = preprocessor.readFile("../LoanData.csv")

    preprocessor.normalizeToTrain(df)
  }

}
