import DataPreprocessor.MLPDataPreprocessor
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.MultilayerPerceptronClassifier
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.sql.SparkSession

object Main {

  def setupLogging(): Unit = {
    import org.apache.log4j.{Level, Logger}
    val rootLogger = Logger.getRootLogger
    rootLogger.setLevel(Level.ERROR)
  }

  def main(args: Array[String]) {

    val spark = SparkSession
      .builder
      .appName("BondoraCreditRiskPrevision")
      .master("local[*]")
      .getOrCreate()

    setupLogging()

    val preprocessor = new MLPDataPreprocessor(spark, new Pipeline())

    val df = preprocessor.readFile("../LoanData.csv")

    val endedLoans = preprocessor.filterEndedLoans(df)

    val normalized = preprocessor.normalize(endedLoans)

    // Split the data into train and test
    val splits = normalized.randomSplit(Array(0.6, 0.4), seed = 1234L)
    val train = splits(0)
    val test = splits(1)

    // specify layers for the neural network:
    val layers = Array[Int](11, 6, 3)

    //creating features column
    val assembler = new VectorAssembler()
      .setHandleInvalid("skip")
      .setInputCols(Array(preprocessor.features: _*))
      .setOutputCol("features")

    // create the trainer and set its parameters
    val trainer = new MultilayerPerceptronClassifier()
      .setLayers(layers)
      .setBlockSize(64)
      .setSeed(1234L)
      .setFeaturesCol("features") // setting features column
      .setLabelCol("Status")
      .setMaxIter(1)

    //creating pipeline
    val pipeline = new Pipeline().setStages(Array(assembler, trainer))

    // train the model
    val model = pipeline.fit(train)

    // compute accuracy on the test set
    val result = model.transform(test)
    val predictionAndLabels = result.select("prediction", "Status")
    val evaluator = new MulticlassClassificationEvaluator()
      .setMetricName("accuracy")
      .setLabelCol("Status")

    println(s"Test set accuracy = ${evaluator.evaluate(predictionAndLabels)}")

  }

}
