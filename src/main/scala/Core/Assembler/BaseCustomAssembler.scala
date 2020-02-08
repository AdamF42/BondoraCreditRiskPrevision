package Core.Assembler

import org.apache.spark.ml.feature.VectorAssembler

trait BaseCustomAssembler {

  def createAssembler(features: Seq[String]):VectorAssembler

}
