package Core.Classifier

import org.apache.spark.ml.PipelineModel
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.sql.DataFrame

trait BaseClassifier {

  def train(trainDf: DataFrame, features: Seq[String]): PipelineModel

  def evaluate(dataFrame: DataFrame, pipelineModel: PipelineModel): Double = {

    val predictionAndLabels = pipelineModel.transform(dataFrame).select("prediction", "Status")
    val evaluator: MulticlassClassificationEvaluator = new MulticlassClassificationEvaluator()
      .setMetricName("accuracy")
      .setLabelCol("Status")

    evaluator.evaluate(predictionAndLabels)
  }

  def classify(): Unit = println("NOT IMPLEMENTED")

}
