package it.unibo.core.assembler

import org.apache.spark.ml.feature.VectorAssembler

trait BaseCustomAssembler {

  def getAssembler(features: Seq[String]): VectorAssembler

}
