import sbt.Keys.libraryDependencies

name := "BondoraCreditRiskPrevision"

version := "0.1"

scalaVersion := "2.12.10"

lazy val circeVersion = "0.11.1"
lazy val sparkVersion = "2.4.4"
lazy val sttpVersion = "2.0.0-RC13"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-streaming" % sparkVersion,
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-mllib" % sparkVersion,
  "com.softwaremill.sttp.client" %% "core" % sttpVersion,
  "com.softwaremill.sttp.client" %% "json4s" % sttpVersion,
  "org.json4s" %% "json4s-native" % "3.6.7",


)

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding",
  "utf-8",
  "-feature",
  "-language:existentials",
  "-language:experimental.macros",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-unchecked",
  "-Xcheckinit",
  "-Xfatal-warnings",
  "-Xfuture",
  "-Xlint"
)

licenses := Seq("The Apache Software License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))