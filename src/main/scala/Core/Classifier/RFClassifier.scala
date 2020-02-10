package Core.Classifier

import org.apache.spark.ml.classification.RandomForestClassifier

private class RFClassifier() extends BaseClassifier {

  override def createTrainer(): RandomForestClassifier =
    new RandomForestClassifier()
      .setImpurity("gini")
      .setMaxDepth(3)
      .setNumTrees(10)
      .setFeaturesCol("features")
      .setSeed(1234L)
      .setLabelCol("Status")

}
