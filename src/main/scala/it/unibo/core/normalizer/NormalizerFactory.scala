package it.unibo.core.normalizer

object NormalizerFactory {

  def apply(): BaseCustomNormalizer =
    new CustomNormalizer()

}
