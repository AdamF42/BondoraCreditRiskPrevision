package Core.Classifier

import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.{Pipeline, PipelineModel, PipelineStage}
import org.apache.spark.sql.DataFrame

trait BaseClassifier {

  def train(df: DataFrame, stages: Array[PipelineStage]): PipelineModel = {

    val trainer = createTrainer()

    new Pipeline()
      .setStages(stages :+ trainer)
      .fit(df)
  }

  def createTrainer(): PipelineStage

  def evaluate(dataFrame: DataFrame, pipelineModel: PipelineModel): Double = {

    val predictionAndLabels = pipelineModel.transform(dataFrame).select("prediction", "Status")
    val evaluator: MulticlassClassificationEvaluator = new MulticlassClassificationEvaluator()
      .setMetricName("accuracy")
      .setLabelCol("Status")

    evaluator.evaluate(predictionAndLabels)
  }

  def classify(): Unit = println("NOT IMPLEMENTED")

}
