package Core.Normalizer

import org.apache.spark.ml.feature.Normalizer

trait BaseCustomNormalizer {

  def getNormalizer(features: Seq[String]): Normalizer

}
