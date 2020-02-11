package Core.Classifier

import org.apache.spark.ml.PipelineModel
import org.apache.spark.sql.DataFrame

trait BaseClassifier {

  def train(trainDf: DataFrame, features: Seq[String]): PipelineModel

  def evaluate(testDf:DataFrame, pipelineModel: PipelineModel): Double

  def classify()

}
