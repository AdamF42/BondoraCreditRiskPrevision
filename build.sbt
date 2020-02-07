import sbt.Keys.libraryDependencies

name := "BondoraCreditRiskPrevision"

version := "0.1"

scalaVersion := "2.12.10"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-streaming" % "2.4.4",
  "org.apache.spark" %% "spark-core" % "2.4.4",
  "org.apache.spark" %% "spark-sql" % "2.4.4" ,
  "org.apache.spark" %% "spark-mllib" % "2.4.4",
  "org.plotly-scala" %% "plotly-core" % "0.7.3"
)
