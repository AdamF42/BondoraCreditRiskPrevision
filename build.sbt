import sbt.Keys.libraryDependencies

name := "BondoraCreditRiskPrevision"

version := "0.1"

scalaVersion := "2.12.10"

lazy val circeVersion = "0.13.0"
lazy val sparkVersion = "2.4.4"
lazy val sttpVersion = "2.0.0-M1"
lazy val mleapVersion = "0.15.0"
lazy val scalacticVersion = "3.1.0"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-streaming" % sparkVersion,
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-mllib" % sparkVersion,
  "com.softwaremill.sttp.client" %% "core" % sttpVersion,
  "com.softwaremill.sttp.client" %% "circe" % sttpVersion,
  "ml.combust.mleap" %% "mleap-spark" % mleapVersion,
  "ml.combust.mleap" %% "mleap-runtime" % mleapVersion,
  "org.scala-lang.modules" %% "scala-xml" % "2.0.0-M1",
  "io.circe" %% "circe-generic" % circeVersion,
  "org.scalactic" %% "scalactic" % scalacticVersion,
  "org.scalatest" %% "scalatest" % scalacticVersion % Test,
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

dependencyOverrides ++= Seq(
  "com.fasterxml.jackson.core" % "jackson-core" % "2.9.8",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.9.8",
  "com.fasterxml.jackson.module" % "jackson-module-scala_2.12" % "2.9.8"
)


licenses := Seq("The Apache Software License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))