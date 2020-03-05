package it.unibo.normalizer

object NormalizerFactory {

  def apply(): BaseCustomNormalizer =
    new CustomNormalizer()

}
