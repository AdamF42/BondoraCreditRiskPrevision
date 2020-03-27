package it.unibo.datapreprocessor

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature.{StringIndexer, VectorAssembler}
import org.apache.spark.ml.linalg.Matrix
import org.apache.spark.ml.stat.Correlation
import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{BooleanType, DataType, DoubleType, StringType}
import org.apache.spark.storage.StorageLevel.MEMORY_AND_DISK_SER_2

private class DataPreprocessor(session: SparkSession) extends BaseDataPreprocessor {


  def readFile(filePath: String): DataFrame =
    session.read.format("csv")
      .option("header", value = true)
      .load(filePath)

  override def normalizeToTrain(df: DataFrame): DataFrame = {

    val dfEndedLoans = filterEndedLoans(df)

    val dfWithoutUselessCols: DataFrame = removeUselessColumns(dfEndedLoans)

    val dfWithDoubleValues: DataFrame = transformValuesToDouble(dfWithoutUselessCols)

    val indexedDf: DataFrame = indexColumnsValues(dfWithDoubleValues)

    val dfReduced: DataFrame = dropColumnsWithHighCorr(indexedDf)

    val meanMap: Map[String, Any] = getMapColumnsMean(dfReduced)

    dfReduced.na.fill(meanMap)
  }

  override def normalizeToClassify(df: DataFrame): DataFrame = {
    val correctTypeDF =
      df.schema.filter(_.dataType == BooleanType).foldLeft(df) {
        case (acc, col) => acc.withColumn(col.name, df(col.name).cast(StringType))
      }

    val indexedDF = indexColumnsValues(correctTypeDF)
      .drop(Columns.getDate: _*)
      .drop(Columns.getUseless.filter(x => !x.contains("UserName")): _*)

    val dfWithMean: DataFrame = loadDataframe() // nome colonna - media

    val meanMap = dfToMap(dfWithMean)

    indexedDF.na.fill(meanMap)
  }

  private def loadDataframe(): DataFrame =
    session.read.format("csv")
      .option("header", value = true)
      .option("compression", "gzip")
      .load("mean")

  private def indexColumnsValues(df: DataFrame): DataFrame = {

    val indexers = createIndex(df)

    val dfIndexed: DataFrame = new Pipeline()
      .setStages(indexers)
      .fit(df)
      .transform(df)
      .drop(Columns.getStrings: _*)
      .persist(MEMORY_AND_DISK_SER_2)

    removeIndexName(dfIndexed)
  }

  private def createIndex(dataFrame: DataFrame): Array[StringIndexer] =
    dataFrame
      .select(Columns.getStrings.head, Columns.getStrings.tail: _*)
      .columns.map { colName =>
      new StringIndexer().setInputCol(colName).setOutputCol(colName + "Index").setHandleInvalid("skip")
    }

  private def removeIndexName(df: DataFrame): DataFrame =
    df.columns
      .foldLeft(df) { (newdf, colname) =>
        newdf.withColumnRenamed(colname, colname
          .replace("Index", ""))
      }

  private def dfToMap(df: DataFrame): Map[String, Any] =
    df.collect.map(r => Map(df.columns.zip(r.toSeq): _*))
      .headOption.getOrElse(Map.empty[String, Double])

  private def saveDataframe(df: DataFrame): Unit =
    df.coalesce(1)
      .write
      .mode(SaveMode.Overwrite)
      .format("com.databricks.spark.csv")
      .option("header", "true")
      .option("compression", "gzip")
      .save("mean")

  private def castAllTypedColumnsTo(df: DataFrame, sourceType: DataType, targetType: DataType): DataFrame = {
    df.schema.filter(_.dataType == sourceType).foldLeft(df) {
      case (acc, col) => acc.withColumn(col.name, df(col.name).cast(targetType))
    }
  }

  private def removeUselessColumns(df: Dataset[Row]): DataFrame = {

    val colsDrop: Array[String] = getColumnsWithNullValues(df)

    df.drop(colsDrop: _*)
      .drop(Columns.getDate: _*)
      .drop(Columns.getUseless: _*)
  }

  private def getMapColumnsMean(df: DataFrame): Map[String, Any] = {

    val media: DataFrame = df.agg(df.columns.map(c => c -> "avg").toMap)

    val mediaRenamed: DataFrame = media.columns
      .foldLeft(media) { (dfTmp, colName) =>
        dfTmp.withColumnRenamed(colName, colName
          .replace("avg(", "")
          .replace(")", ""))
      }

    saveDataframe(mediaRenamed) // nomeColonna    nomeColonna
    //    media          media
    dfToMap(mediaRenamed)
  }

  private def transformValuesToDouble(df: DataFrame): DataFrame = {

    val dfChangeNumType: DataFrame = castAllTypedColumnsTo(
      df.select(Columns.getDouble.head, Columns.getDouble.tail: _*), StringType, DoubleType)
      .withColumn("id", monotonically_increasing_id())

    val dfLexical: DataFrame = df
      .withColumn("id", monotonically_increasing_id())
      .drop(Columns.getDouble: _*)

    dfChangeNumType.join(dfLexical, Seq("id")).drop("id")
  }

  private def getColumnsWithNullValues(dfEndedLoans: Dataset[Row]): Array[String] = {

    val nullValuePerCol = countNullValue(dfEndedLoans)
    val nullThreshold = 20 * dfEndedLoans.count / 100

    val columnsToDrop = nullValuePerCol.filter { case (_, numOfNull) => numOfNull >= nullThreshold }

    columnsToDrop.map { case (colName, _) => colName }
  }

  @scala.annotation.tailrec
  private def dropColumnsWithHighCorr(df: DataFrame): DataFrame = {

    val highCorrelatedColumns: DataFrame = getCorrelatedColumns(df)
      .filter(col("Correlation")
        .between(0.7, 1))

    highCorrelatedColumns.count match {
      case 0 => df
      case _ =>
        val mostCorrelatedCol: String = highCorrelatedColumns
          .orderBy(desc("Correlation"))
          .select(col("item_from"))
          .first.getString(0)
        dropColumnsWithHighCorr(df.drop(mostCorrelatedCol))
    }
  }

  private def getCorrelatedColumns(df: DataFrame): DataFrame = {

    val correlationMatrix: Matrix = getCorrelationMatrix(df)

    val colNamePairs = df.columns.flatMap(c1 => df.columns.map(c2 => (c1, c2)))

    val triplesList: List[(String, String, Double)] = colNamePairs.zip(correlationMatrix.toArray)
      .filterNot { case ((itemFrom, itemTo), _) => itemFrom >= itemTo }
      .map { case ((itemFrom, itemTo), corrValue) => (itemFrom, itemTo, corrValue) }
      .toList

    val corrValue: DataFrame = session.createDataFrame(triplesList)
      .toDF("item_from", "item_to", "Correlation")
      .withColumn("Correlation", expr("abs(Correlation)"))

    corrValue
  }

  private def getCorrelationMatrix(df: DataFrame): Matrix = {

    val dfAssembled: DataFrame = new VectorAssembler()
      .setHandleInvalid("skip")
      .setInputCols(df.columns)
      .setOutputCol("corr_columns")
      .transform(df)
      .select("corr_columns")

    val correlationMatrix: Row = Correlation.corr(dfAssembled, "corr_columns").first // spearman

    correlationMatrix.getAs(0)
  }

  private def filterEndedLoans(df: DataFrame): Dataset[Row] =
    df.select(df.columns.head, df.columns.tail: _*)
      .where(df.col("Status").isin(List("Late", "Repaid"): _*))

  private def countNullValue(df: DataFrame): Array[(String, Long)] = {
    df.columns
      .map(x => (x, df.filter(df(x).isNull || df(x) === "" || df(x).isNaN).count))
  }
}