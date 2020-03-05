package it.unibo.classifier

import org.apache.spark.ml.classification.RandomForestClassifier

private class RFClassifier(val rfClassifier: RandomForestClassifier = new RandomForestClassifier())
  extends BaseClassifier {

  override def createTrainer(): RandomForestClassifier =
    rfClassifier
      .setImpurity("gini")
      .setMaxDepth(3)
      .setNumTrees(10)
      .setFeaturesCol("features")
      .setSeed(1234L)
      .setLabelCol("Status")

  override def getSavedModelName: String = "/rf.zip"
}
