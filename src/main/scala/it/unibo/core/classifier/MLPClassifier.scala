package it.unibo.core.classifier

import org.apache.spark.ml.classification.MultilayerPerceptronClassifier

private class MLPClassifier() extends BaseClassifier {

  override def createTrainer(): MultilayerPerceptronClassifier =
    new MultilayerPerceptronClassifier()
      .setLayers(Array[Int](43, 20, 10, 3))
      .setBlockSize(64)
      .setSeed(1234L)
      .setFeaturesCol("norm_features")
      .setLabelCol("Status")
      .setMaxIter(1)

}
