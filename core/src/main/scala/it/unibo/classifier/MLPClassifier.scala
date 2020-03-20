package it.unibo.classifier

import org.apache.spark.ml.classification.MultilayerPerceptronClassifier

private class MLPClassifier(val mlpClassifier: MultilayerPerceptronClassifier = new MultilayerPerceptronClassifier)
  extends BaseClassifier {

  override def createTrainer(): MultilayerPerceptronClassifier =
    this.mlpClassifier
      .setLayers(Array[Int](45, 22, 11, 2))
      .setBlockSize(64)
      .setSeed(1234L)
      .setFeaturesCol("norm_features")
      .setLabelCol("Status")
      .setMaxIter(10)

  override def getSavedModelName: String = "/mlp.zip"
}
