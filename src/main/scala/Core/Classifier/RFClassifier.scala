package Core.Classifier

import Core.Assembler.BaseCustomAssembler
import org.apache.spark.ml.classification.RandomForestClassifier
import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.sql.DataFrame

private class RFClassifier(assembler: BaseCustomAssembler) extends BaseClassifier {

  override def train(df: DataFrame, features: Seq[String]): PipelineModel = {

    val vectorAssembler = assembler.createAssembler(features)

    val trainer = createTrainer

    new Pipeline()
      .setStages(Array(vectorAssembler, trainer))
      .fit(df)
  }

  private def createTrainer: RandomForestClassifier =
    new RandomForestClassifier()
      .setImpurity("gini")
      .setMaxDepth(3)
      .setNumTrees(10)
      .setFeaturesCol("features")
      .setSeed(1234L)
      .setLabelCol("Status")

}
