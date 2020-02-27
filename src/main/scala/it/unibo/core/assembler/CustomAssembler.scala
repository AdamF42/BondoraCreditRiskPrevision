package it.unibo.core.assembler

import org.apache.spark.ml.feature.VectorAssembler

class CustomAssembler extends BaseCustomAssembler {

  override def getAssembler(features: Seq[String]): VectorAssembler =
    new VectorAssembler()
      .setInputCols(Array(features: _*))
      .setOutputCol("features")
}
