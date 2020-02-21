import sbt.Keys.libraryDependencies

name := "BondoraCreditRiskPrevision"

version := "0.1"

scalaVersion := "2.12.10"

lazy val akkaVersion = "2.6.3"
lazy val akkaHttpVersion = "10.1.11"
lazy val circeVersion    = "0.11.1"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-streaming" % "2.4.4",
  "org.apache.spark" %% "spark-core" % "2.4.4",
  "org.apache.spark" %% "spark-sql" % "2.4.4" ,
  "org.apache.spark" %% "spark-mllib" % "2.4.4",
  "org.plotly-scala" %% "plotly-core" % "0.7.3",
  "com.typesafe.akka" %% "akka-stream"                 % akkaVersion,
  "com.typesafe.akka" %% "akka-http"                   % akkaHttpVersion,
  "io.circe"          %% "circe-generic"               % circeVersion,
  "io.circe"          %% "circe-parser"                % circeVersion,
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