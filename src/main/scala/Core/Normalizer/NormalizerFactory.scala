package Core.Normalizer

object NormalizerFactory {

  def apply(): BaseCustomNormalizer =
    new CustomNormalizer()

}
