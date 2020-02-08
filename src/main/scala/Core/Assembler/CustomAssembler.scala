package Core.Assembler

import org.apache.spark.ml.feature.VectorAssembler

class CustomAssembler extends BaseCustomAssembler {

  override def createAssembler(features: Seq[String]): VectorAssembler =
    new VectorAssembler()
      .setHandleInvalid("skip")
      .setInputCols(Array(features: _*))
      .setOutputCol("features")
}
