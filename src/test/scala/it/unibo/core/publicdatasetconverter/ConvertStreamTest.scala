package it.unibo.core.publicdatasetconverter

import it.unibo.client.model.{PublicDataset, PublicDatasetPayload}
import it.unibo.core.publicdatasetconvert.ConvertStream
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec


class ConvertStreamTest extends AnyWordSpec with Matchers {

  implicit val session = SparkSession
    .builder
    .appName("BondoraCreditRiskPrevision")
    .master("local[*]")
    .getOrCreate()

  "blabla" should {
    "bla" in {
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

      val publicDataInput = new PublicDataset(0,0,0,0,Seq(payloadInput),Some(true),None)

      val dfResult = ConvertStream.readStream(publicDataInput)

      val dfExpected = session.read.format("csv")
        .option("header", true)
        .option("inferSchema", "true")
        .load("/home/laura/Projects/BondoraCreditRiskPrevision/BondoraCreditRiskPrevision/src/test/scala/it/unibo/core/publicdatasetconverter/test.csv")

      dfResult shouldBe dfExpected
      

    }
  }
}
