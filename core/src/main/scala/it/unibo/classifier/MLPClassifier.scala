package it.unibo.classifier

import org.apache.spark.ml.classification.MultilayerPerceptronClassifier

private class MLPClassifier(val mlpClassifier: MultilayerPerceptronClassifier = new MultilayerPerceptronClassifier)
  extends BaseClassifier {

  override def createTrainer(): MultilayerPerceptronClassifier =
    this.mlpClassifier
      .setLayers(Array[Int](43, 20, 10, 3))
      .setBlockSize(64)
      .setSeed(1234L)
      .setFeaturesCol("norm_features")
      .setLabelCol("Status")
      .setMaxIter(1)

  override def getSavedModelName: String = "/mlp.zip"
}
