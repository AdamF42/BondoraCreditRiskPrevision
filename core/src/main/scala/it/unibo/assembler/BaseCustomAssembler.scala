package it.unibo.assembler

import org.apache.spark.ml.feature.VectorAssembler

trait BaseCustomAssembler {

  def getAssembler(features: Seq[String]): VectorAssembler

}
