package Core.DataPreprocessor

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature.{StringIndexer, VectorAssembler}
import org.apache.spark.ml.linalg.Matrix
import org.apache.spark.ml.stat.Correlation
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{DataType, DoubleType, StringType}
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

private class DataPreprocessor(session: SparkSession) extends BaseDataPreprocessor {

  def readFile(filePath: String): DataFrame =
    session.read.format("csv")
      .option("header", value = true)
      .load(filePath)

  override def normalize(df: DataFrame): DataFrame = {

    val dfEndedLoans = filterEndedLoans(df)

    val dfWithoutUselessCols: DataFrame = removeUselessColumns(dfEndedLoans)

    val dfWithDoubleValues: DataFrame = transformValuesToDouble(dfWithoutUselessCols)

    val indexedDf: DataFrame = indexColumnsValues(dfWithDoubleValues)

    val dfReduced: DataFrame = dropColumnsWithHighCorr(indexedDf)

    val map: Map[String, Any] = getMapColumnsMean(dfReduced)

    dfReduced.na.fill(map)
  }

  private def removeUselessColumns(df: Dataset[Row]) = {
    val colsDrop: Array[String] = getColumnsWithNullValues(df)

    val dfWithoutUselessCols = df
      .drop(colsDrop: _*)
      .drop(Columns.getDate: _*)
      .drop(Columns.getUseless: _*)
    dfWithoutUselessCols
  }

  private def getMapColumnsMean(df: DataFrame) = {
    val nullValue = countNullValue(df)
      .filter(y => y._2 > 0)
      .map(z => z._1)

    val media = df.select(nullValue.toSeq.map(mean): _*)

    val mediaRenamed = media
      .columns
      .foldLeft(media) { (dfTmp, colName) =>
        dfTmp.withColumnRenamed(colName, colName
          .replace("avg(", "")
          .replace(")", ""))
      }

    val meanValues: Array[Map[String, Any]] = mediaRenamed.collect.map(r => Map(mediaRenamed.columns.zip(r.toSeq): _*))

    val map = meanValues.head
    map
  }

  private def indexColumnsValues(df:DataFrame) = {
    val indexers = createIndex(df)

    val dfIndexed: DataFrame = new Pipeline()
      .setStages(indexers)
      .fit(df)
      .transform(df)
      .drop(Columns.getStrings: _*)
      .cache()

    val dfCorrectColumnsName = dfIndexed
      .columns
      .foldLeft(dfIndexed) { (newdf, colname) =>
        newdf.withColumnRenamed(colname, colname
          .replace("Index", ""))
      }
    dfCorrectColumnsName

  }

  private def transformValuesToDouble(df: DataFrame) = {
    val numericalCols: Seq[String] = df.columns.filter(c => !Columns.getStrings.contains(c))
    val dfNumerical: DataFrame = df.drop(Columns.getStrings: _*)
    val dfChangeNumType: DataFrame = castAllTypedColumnsTo(dfNumerical, StringType, DoubleType)
        .withColumn("id", monotonically_increasing_id())

    //val tmpdfNumerical = dfChangeNumType.withColumn("id", monotonically_increasing_id())

    val dfLexical: DataFrame = df.drop(numericalCols: _*)
    val tmpDFString = dfLexical.withColumn("id", monotonically_increasing_id())

    dfChangeNumType.join(tmpDFString, Seq("id")).drop("id")
  }

  private def getColumnsWithNullValues(dfEndedLoans: Dataset[Row]) = {

    val nullValuePerCol = countNullValue(dfEndedLoans)
    val nullThreshold = 20 * dfEndedLoans.count / 100

    val columnsToDrop = nullValuePerCol.filter(x => x._2 >= nullThreshold)

    columnsToDrop.map(x => x._1)

  }

  @scala.annotation.tailrec
  private def dropColumnsWithHighCorr(df: DataFrame): DataFrame = {

    val correlatedColumns: DataFrame = getCorrelatedColumns(df)

    val highCorrelatedColumns: DataFrame = correlatedColumns.filter(col("Correlation").between(0.7, 1))

    if (highCorrelatedColumns.count > 0) {
      val mostCorrelatedCol: String = highCorrelatedColumns
        .orderBy(desc("Correlation"))
        .select(col("item_from"))
        .first.getString(0)
      dropColumnsWithHighCorr(df.drop(mostCorrelatedCol))
    }
    else
      df
  }

  private def getCorrelatedColumns(df: DataFrame) = {

    val correlationMatrix: Matrix = getCorrelationMatrix(df)

    val colNamePairs = df.columns.flatMap(c1 => df.columns.map(c2 => (c1, c2)))

    val triplesList: List[(String, String, Double)] = colNamePairs.zip(correlationMatrix.toArray)
      .filterNot(p => p._1._1 >= p._1._2)
      .map(r => (r._1._1, r._1._2, r._2))
      .toList

    val corrValue: DataFrame = session.createDataFrame(triplesList)
      .toDF("item_from", "item_to", "Correlation")
      .withColumn("Correlation", expr("abs(Correlation)"))

    corrValue
  }

  private def getCorrelationMatrix(df: DataFrame) = {
    val dfAssembled: DataFrame = new VectorAssembler()
      .setHandleInvalid("skip")
      .setInputCols(df.columns)
      .setOutputCol("corr_columns")
      .transform(df)
      .select("corr_columns")

    val corr: Row = Correlation.corr(dfAssembled, "corr_columns").head // spearman

    val matrixCorr: Matrix = corr.getAs(0)
    matrixCorr
  }

  private def filterEndedLoans(df: DataFrame): Dataset[Row] = {
    df.select(df.columns.head, df.columns.tail: _*)
      .where(df.col("ContractEndDate").lt(lit(current_date())))
  }

  private def countNullValue(df: DataFrame): Array[(String, Long)] = {
    df
      .columns
      .map(x => (x, df.filter(df(x).isNull || df(x) === "" || df(x).isNaN).count))
  }

  private def castAllTypedColumnsTo(df: DataFrame, sourceType: DataType, targetType: DataType): DataFrame = {
    df.schema.filter(_.dataType == sourceType).foldLeft(df) {
      case (acc, col) => acc.withColumn(col.name, df(col.name).cast(targetType))
    }
  }

  private def createIndex(dataFrame: DataFrame) =
    dataFrame
      .select(Columns.getStrings.head, Columns.getStrings.tail: _*)
      .columns.map { colName =>
      new StringIndexer().setInputCol(colName).setOutputCol(colName + "Index").setHandleInvalid("skip")
    }
}