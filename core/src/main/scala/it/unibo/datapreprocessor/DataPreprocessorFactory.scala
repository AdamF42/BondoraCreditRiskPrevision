package it.unibo.datapreprocessor

import it.unibo.sparksession.Configuration

object DataPreprocessorFactory {

  def apply()(implicit sparkConfiguration: Configuration): BaseDataPreprocessor =
    new DataPreprocessor(sparkConfiguration.getOrCreateSession)

}
