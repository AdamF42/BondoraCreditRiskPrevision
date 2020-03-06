package it.unibo.converter

import it.unibo.client.model.PublicDatasetPayload
import org.apache.spark.sql.types.{DataType, StringType}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec


class PublicDatasetPayloadConverterTest extends AnyWordSpec with Matchers {

  implicit val session: SparkSession = setupSparkSession
  val payloadInput = new PublicDatasetPayload(
    Some("8a9b8f39-824a-48bf-bfe2-0f5ea2ab4b87"),
    Some(482243),
    Some("2016-03-21T14:30:36"),
    Some("2016-03-21T14:30:36"),
    Some(8025),
    Some(5),
    Some(475),
    Some("BO25A7433"),
    Some(false),
    Some("2016-03-21T14:03:23"),
    Some("2016-03-23T00:00:00"),
    Some("2017-02-08T00:00:00"),
    Some("2016-05-16T00:00:00"),
    Some("2021-04-15T00:00:00"),
    Some("2021-10-15T00:00:00"),
    Some(11),
    Some(4),
    Some(4),
    Some(4),
    Some(44),
    Some("1971-09-14T00:00:00"),
    Some(1),
    Some("FI"),
    Some("UUSIMAA"),
    Some("ESPOO"),
    Some(8505),
    Some(8505),
    Some(24.52),
    Some(60),
    Some(266),
    Some(7),
    Some(4),
    Some(4),
    Some(0),
    Some(3),
    Some("MoreThan5Years"),
    Some("Worker"),
    Some("15To25Years"),
    Some(17),
    Some(1),
    Some(1760),
    Some(0),
    Some(0),
    Some(0),
    Some(0),
    Some(0),
    Some(0),
    Some(1760),
    Some(15),
    Some(2),
    Some(1896.61),
    Some(65.7),
    Some(75.72),
    Some(15),
    Some(true),
    Some(8284.9),
    Some(623.03),
    Some("2017-02-08T00:00:00"),
    None,
    None,
    None,
    None,
    Some(0.11733911955697243),
    Some(0.68),
    Some(0.12784320600306903),
    Some(0.17086494065809449),
    None,
    None,
    None,
    None,
    None,
    None,
    None,
    None,
    None,
    Some("0001-01-01T00:00:00"),
    Some(2),
    Some("D"),
    None,
    None,
    None,
    None,
    Some(0.11733911955697243),
    Some("D"),
    Some(false),
    Some("Repaid"),
    Some(true),
    None,
    Some("31-60"),
    None,
    None,
    Some("RL2"),
    None,
    Some(8505),
    Some(1785.97),
    Some(0),
    Some(0),
    Some(0),
    Some(0),
    Some(0),
    Some(0),
    Some(1),
    Some(2500),
    Some(518.53),
    Some(0),
    Some(0),
    None,
    None,
    None,
    None,
    None,
    None
  )
  val dfResult: DataFrame = PublicDatasetPayloadConverter.publicDStoDF(Seq(payloadInput))
  val dfExpected: DataFrame = session.read.format("csv")
    .option("header", value = true)
    .load("ec2/src/test/scala/it/unibo/publicdatasetconvert/test.csv")

  def setupSparkSession: SparkSession = {

    val session = SparkSession
      .builder
      .master("local[*]")
      .getOrCreate()
    import org.apache.log4j.{Level, Logger}
    val rootLogger = Logger.getRootLogger
    rootLogger.setLevel(Level.ERROR)
    session
  }

  "ConvertPublicDatasetPayload" should {
    "return a dataset with correct values in rows" in {

      val sortedCols: Seq[String] = dfResult.columns.sorted.toSeq
      val dfResultToString = castAllTypedColumnsTo(dfResult, StringType)

      val dfResultOrdered = dfResultToString.select(sortedCols.head, sortedCols.tail: _*)
      val dfExpectedOrder = dfExpected.select(sortedCols.head, sortedCols.tail: _*)

      val dfSubtraction1 = dfExpectedOrder.except(dfResultOrdered)
      val dfSubtraction2 = dfResultOrdered.except(dfExpectedOrder)

      dfSubtraction1.count() shouldBe 0
      dfSubtraction2.count() shouldBe 0

    }
  }

  "ConvertPublicDatasetPayload" should {
    "return a dataset with correct columns" in {
      dfResult.columns.sorted.toSeq shouldBe dfExpected.columns.sorted.toSeq
    }
  }

  "ConvertPublicDatasetPayload" should {
    "has no rows when payload is null" in {
      val dfResult: DataFrame = PublicDatasetPayloadConverter.publicDStoDF(Seq.empty)
      dfResult.head(1).isEmpty shouldBe true
    }
  }

  "ConvertPublicDatasetPayload" should {
    "has no columns when payload is null" in {
      val dfResult: DataFrame = PublicDatasetPayloadConverter.publicDStoDF(Seq.empty)
      dfResult.columns.toSeq.isEmpty shouldBe true
    }
  }

  private def castAllTypedColumnsTo(df: DataFrame, targetType: DataType): DataFrame = {
    df.schema.foldLeft(df) {
      case (acc, col) => acc.withColumn(col.name, df(col.name).cast(targetType))
    }
  }

}
