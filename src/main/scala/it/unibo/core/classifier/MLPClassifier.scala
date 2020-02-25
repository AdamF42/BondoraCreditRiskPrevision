package it.unibo.core.classifier

import org.apache.spark.ml.classification.MultilayerPerceptronClassifier

private class MLPClassifier() extends BaseClassifier {

  private val mlpClassifier = new MultilayerPerceptronClassifier()

  override def createTrainer(): MultilayerPerceptronClassifier =
    mlpClassifier
      .setLayers(Array[Int](43, 20, 10, 3))
      .setBlockSize(64)
      .setSeed(1234L)
      .setFeaturesCol("norm_features")
      .setLabelCol("Status")
      .setMaxIter(1)

  override def getSavedModelName(): String = "mlp.zip"
}
