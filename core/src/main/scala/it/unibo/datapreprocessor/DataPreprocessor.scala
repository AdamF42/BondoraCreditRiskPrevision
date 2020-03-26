package it.unibo.datapreprocessor

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature.{StringIndexer, VectorAssembler}
import org.apache.spark.ml.linalg.Matrix
import org.apache.spark.ml.stat.Correlation
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{BooleanType, DataType, DoubleType, StringType}
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}
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

    meanMap.foreach(x => println(s"${x._1} -> ${x._2}"))

    saveDataframe(meanMap)

    val ciccia = loadDataframe()
    ciccia.show()

    dfReduced.na.fill(meanMap)
  }

  private def saveDataframe(ciccio: Map[String, Any]): Unit = {
    import session.implicits._
    // session.sparkContext.parallelize(map.toSeq).saveAsTextFile("token_freq")
    // session.sparkContext.parallelize(map.toSeq).saveAsObjectFile("meanMap")
    session.sparkContext.parallelize(ciccio.toSeq).coalesce(1).toDF()
      .write
      .option("header", "true")
      .csv("meanMap")
  }

  private def loadDataframe(): DataFrame = {
//    type MeanMap = (String, Any)
//    val boh = session.sparkContext.objectFile[MeanMap]("meanMap").collectAsMap()
//    boh
    session.read.format("csv")
      .option("header", value = true)
      .load("meanMap.csv")
  }

  override def normalizeToClassify(df: DataFrame): DataFrame = {

    println("sono in normalizeToClassify")
    val correctTypeDF =
      df.schema.filter(_.dataType == BooleanType).foldLeft(df) {
        case (acc, col) => acc.withColumn(col.name, df(col.name).cast(StringType))
      }

    val banano = indexColumnsValues(correctTypeDF)
      .drop(Columns.getDate: _*)
      .drop(Columns.getUseless.filter(x => !x.contains("UserName")): _*)

    val adam = loadDataframe() // Map [String, Any]

    banano.na

    val valoriNulli = countNullValue(banano)
    valoriNulli.foreach(println)

    println(banano.columns.length)

    banano.show()

    banano

  }



//    val correctTypeDF = transformValuesToString(df)
//
//    indexColumnsValues(correctTypeDF)
//      .drop(Columns.getDate: _*)
//      .drop(Columns.getUseless.filter(x => !x.contains("UserName")): _*)
//  }
//
//  private def transformValuesToString(df: DataFrame): DataFrame = {
//    val dfChangeBoolType: DataFrame = castAllTypedColumnsTo(
//      df.select(Columns.getBoolean.head, Columns.getBoolean.tail: _*), BooleanType, StringType)
//      .withColumn("id", monotonically_increasing_id())
//
//    val stringDF = df
//      .withColumn("id", monotonically_increasing_id())
//      .drop(Columns.getBoolean: _*)
//
//    stringDF.join(dfChangeBoolType, Seq("id")).drop("id")
//  }


//    val df2 = df
//      .withColumn("ActiveScheduleFirstPaymentReached", col("ActiveScheduleFirstPaymentReached").cast(StringType))
//      .withColumn("NewCreditCustomer", col("NewCreditCustomer").cast(StringType))
//      .withColumn("Restructured", col("Restructured").cast(StringType))
//      .withColumn("LoanCancelled", col("LoanCancelled").cast(StringType))
//ColumnsValues(df2)
  ////      .drop(Columns.getDate: _*)
  ////      .drop(Columns.getUseless.filter(x => !x.contains("UserName")): _*)
  ////
  ////    countNullValue(banano).foreach(println)
  ////
  ////    banano
//    val banano = index



  private def castAllTypedColumnsTo(df: DataFrame, sourceType: DataType, targetType: DataType): DataFrame = {
    df.schema.filter(_.dataType == sourceType).foldLeft(df) {
      case (acc, col) => acc.withColumn(col.name, df(col.name).cast(targetType))
    }
  }

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

  private def removeIndexName(df: DataFrame): DataFrame = {
    df.columns
      .foldLeft(df) { (newdf, colname) =>
        newdf.withColumnRenamed(colname, colname
          .replace("Index", ""))
      }
  }

  private def removeUselessColumns(df: Dataset[Row]): DataFrame = {

    val colsDrop: Array[String] = getColumnsWithNullValues(df)

    df.drop(colsDrop: _*)
      .drop(Columns.getDate: _*)
      .drop(Columns.getUseless: _*)
  }

  private def getMapColumnsMean(df: DataFrame): Map[String, Any] = {

//    val nullValue = countNullValue(df)
//      .filter { case (_, nullCount) => nullCount > 0 }
//      .map { case (columnName, _) => columnName }

    val media = df.select(df.columns.toSeq.map(mean): _*)

    val mediaRenamed = media.columns
      .foldLeft(media) { (dfTmp, colName) =>
        dfTmp.withColumnRenamed(colName, colName
          .replace("avg(", "")
          .replace(")", ""))
      }

    mediaRenamed
      .collect.map(r => Map(mediaRenamed.columns.zip(r.toSeq): _*))
      .headOption.getOrElse(Map.empty[String, Any])
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

  private def filterEndedLoans(df: DataFrame): Dataset[Row] = {
    df.select(df.columns.head, df.columns.tail: _*)
      .where(df.col("Status").isin(List("Late","Repaid"):_*))
  }

  private def countNullValue(df: DataFrame): Array[(String, Long)] = {
    df.columns
      .map(x => (x, df.filter(df(x).isNull || df(x) === "" || df(x).isNaN).count))
  }
}