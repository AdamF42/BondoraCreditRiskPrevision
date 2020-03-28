package it.unibo.classifier

import ml.combust.bundle.BundleFile
import ml.combust.bundle.serializer.SerializationFormat
import ml.combust.mleap.spark.SparkSupport._
import org.apache.spark.ml.bundle.SparkBundleContext
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.{Pipeline, PipelineStage, Transformer}
import org.apache.spark.sql.DataFrame
import resource.managed

trait BaseClassifier {

  private val baseDir: String = s"${System.getProperty("user.dir")}/models"
  private val modelName: String = getSavedModelName
  private val trainer: PipelineStage = createTrainer()
  private val predictionToLabel: Map[Double, String] = collection.immutable.HashMap(
    0.0 -> "Late",
    1.0 -> "Repaid"
  )
  private var pipelineModel: Option[Transformer] = None
  private var trainDataFrame: Option[DataFrame] = None

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

    val model: Transformer = pipelineModel.getOrElse(throw new ClassNotFoundException)

    val classified: DataFrame = model.transform(df).select("prediction", "Status")

    val pred = classified.select("prediction").collect
      .map(x => predictionToLabel(x.getAs[Double]("prediction"))).toList

    val user = df.select("UserName").collect
      .map(each => each.getAs[String]("UserName")).toList

    user zip pred
  }

  def saveModel(): Unit = {

    val train = trainDataFrame.getOrElse(throw new ClassNotFoundException)
    val model = pipelineModel.getOrElse(throw new ClassNotFoundException)

    val contextBundle = SparkBundleContext().withDataset(model.transform(train))

    for (bundle <- managed(BundleFile("jar:file:" + baseDir + modelName))) {
      model.writeBundle.format(SerializationFormat.Json).save(bundle)(contextBundle)
        .getOrElse(throw new NoSuchElementException)
    }
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
