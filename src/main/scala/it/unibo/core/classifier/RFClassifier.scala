package it.unibo.core.classifier

import org.apache.spark.ml.classification.RandomForestClassifier

private class RFClassifier() extends BaseClassifier {

  private val rfClassifier = new RandomForestClassifier()

  override def createTrainer(): RandomForestClassifier =
    rfClassifier
      .setImpurity("gini")
      .setMaxDepth(3)
      .setNumTrees(10)
      .setFeaturesCol("features")
      .setSeed(1234L)
      .setLabelCol("Status")

  override def getSavedModelName(): String = "rf.zip"
}
