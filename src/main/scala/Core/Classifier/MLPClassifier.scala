package Core.Classifier

import org.apache.spark.ml.classification.MultilayerPerceptronClassifier
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.sql.DataFrame

private class MLPClassifier extends BaseClassifier {

  override def train(df: DataFrame, features: Seq[String]): PipelineModel = {

    val assembler: VectorAssembler = createAssembler(features)

    val trainer = createTrainer

    new Pipeline()
      .setStages(Array(assembler, trainer))
      .fit(df)
  }

  private def createTrainer: MultilayerPerceptronClassifier =
    new MultilayerPerceptronClassifier()
      .setLayers(Array[Int](11, 6, 3))
      .setBlockSize(64)
      .setSeed(1234L)
      .setFeaturesCol("features")
      .setLabelCol("Status")
      .setMaxIter(1)

  private def createAssembler(features: Seq[String]) =
    new VectorAssembler()
      .setHandleInvalid("skip")
      .setInputCols(Array(features: _*))
      .setOutputCol("features")

  override def classify(): Unit = println("NOT IMPLEMENTED")
  
  override def evaluate(dataFrame: DataFrame, pipelineModel: PipelineModel): Double = {

    val predictionAndLabels = pipelineModel.transform(dataFrame).select("prediction", "Status")
    val evaluator: MulticlassClassificationEvaluator = new MulticlassClassificationEvaluator()
      .setMetricName("accuracy")
      .setLabelCol("Status")

    evaluator.evaluate(predictionAndLabels)
  }
}
