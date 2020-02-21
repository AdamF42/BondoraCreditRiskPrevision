package Core.Normalizer

import org.apache.spark.ml.feature.Normalizer

class CustomNormalizer extends BaseCustomNormalizer {

  override def getNormalizer(features: Seq[String]): Normalizer =
    new Normalizer()
      .setInputCol("features")
      .setOutputCol("norm_features")
      .setP(1.0)
}
