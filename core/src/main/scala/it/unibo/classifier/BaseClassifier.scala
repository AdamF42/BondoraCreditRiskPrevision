package it.unibo.classifier

import it.unibo.core.datapreprocessor.Columns
import ml.combust.bundle.BundleFile
import ml.combust.bundle.serializer.SerializationFormat
import ml.combust.mleap.spark.SparkSupport._
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.ml.bundle.SparkBundleContext
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.StringIndexer
import org.apache.spark.ml.{Pipeline, PipelineStage, Transformer}
import org.apache.spark.sql.functions.monotonically_increasing_id
import org.apache.spark.sql.types.{BooleanType, DataType, StringType}
import org.apache.spark.sql.{DataFrame, SparkSession}
import resource.managed

trait BaseClassifier {

  private val baseDir: String = System.getProperty("user.dir") + "/models"
  private val modelName: String = getSavedModelName
  private val trainer: PipelineStage = createTrainer()
  private var pipelineModel: Option[Transformer] = None
  private var trainDataFrame: Option[DataFrame] = None
  private val predictionToLabel: Map[Double, String] = collection.immutable.HashMap(
    0.0 -> "Late",
    1.0 -> "Repaid"
  )

  def getSavedModelName: String

  def train(df: DataFrame, stages: Array[PipelineStage]): BaseClassifier = {

    this.trainDataFrame = Some(df)

    this.pipelineModel = Some(new Pipeline()
      .setStages(stages :+ trainer)
      .fit(df))
    this
  }

  def createTrainer(): PipelineStage

  def evaluate(df: DataFrame): Double = {

    val transformer = pipelineModel.getOrElse(throw new ClassNotFoundException)
    val predictionAndLabels = transformer.transform(df).select("prediction", "Status")

    val evaluator: MulticlassClassificationEvaluator = new MulticlassClassificationEvaluator()
      .setMetricName("accuracy")
      .setLabelCol("Status")

    evaluator.evaluate(predictionAndLabels)
  }

  def classify(df: DataFrame): List[(String, String)] = {

    def castAllTypedColumnsTo(df: DataFrame, sourceType: DataType, targetType: DataType): DataFrame = {
      df.schema.filter(_.dataType == sourceType).foldLeft(df) {
        case (acc, col) => acc.withColumn(col.name, df(col.name).cast(targetType))
      }
    }

    def cleanDF(df: DataFrame): DataFrame = {
      val booleanDF = df
        .withColumn("id", monotonically_increasing_id())
        .drop(Columns.getBoolean: _*)

      val dfChangeBoolType: DataFrame = castAllTypedColumnsTo(
        df.select(Columns.getBoolean.head, Columns.getBoolean.tail: _*), BooleanType, StringType)
        .withColumn("id", monotonically_increasing_id())

      booleanDF.join(dfChangeBoolType, Seq("id")).drop("id")
    }

    val finalDF = cleanDF(df)

    val indexer: Array[StringIndexer] = finalDF
      .select(Columns.getStrings.head, Columns.getStrings.tail: _*)
      .columns.map { colName =>
      new StringIndexer().setInputCol(colName).setOutputCol(colName + "Index").setHandleInvalid("skip")
    }

    val testData: DataFrame = new Pipeline()
      .setStages(indexer)
      .fit(finalDF)
      .transform(finalDF)
      .drop(Columns.getStrings: _*)
      .drop(Columns.getDate: _*)
      .drop(Columns.getUseless.filter(x => !x.contains("UserName")): _*)

    val test: DataFrame = testData.columns
      .foldLeft(testData) { (newdf, colname) =>
        newdf.withColumnRenamed(colname, colname
          .replace("Index", ""))
      }

    val model: Transformer = pipelineModel.getOrElse(throw new ClassNotFoundException)

    val classified: DataFrame = model.transform(test).select("prediction", "Status")

    val pred = classified.select("prediction").collect
      .map(x => predictionToLabel(x.getAs[Double]("prediction"))).toList

    val user = df.select("UserName").collect
      .map(each => each.getAs[String]("UserName")).toList

    user zip pred
  }

  def saveModel()(implicit spark: SparkSession): Unit = {

    setupDirectory(spark)

    val train = trainDataFrame.getOrElse(throw new ClassNotFoundException)
    val model = pipelineModel.getOrElse(throw new ClassNotFoundException)

    val contextBundle = SparkBundleContext().withDataset(model.transform(train))

    for (bundle <- managed(BundleFile("jar:file:" + baseDir + modelName))) {
      model.writeBundle.format(SerializationFormat.Json).save(bundle)(contextBundle)
        .getOrElse(throw new NoSuchElementException)
    }
  }

  private def setupDirectory(implicit spark: SparkSession): Unit = {
    val fs: FileSystem = FileSystem.get(spark.sparkContext.hadoopConfiguration)
    val outPutPath = new Path(baseDir)
    val filePath = new Path(baseDir + modelName)

    if (fs.exists(filePath))
      fs.delete(filePath, false)

    if (!fs.exists(outPutPath))
      fs.mkdirs(outPutPath)
  }

  def loadModel(): Transformer = {
    val uriPath = "jar:file:" + baseDir + modelName
    val model = (for (bundle <- managed(BundleFile(uriPath))) yield {
      bundle.loadSparkBundle().getOrElse(throw new NoSuchElementException)
    }).opt.getOrElse(throw new NoSuchElementException).root

    this.pipelineModel = Some(model)

    model
  }

}
