import sbt.Keys.libraryDependencies

name := "BondoraCreditRiskPrevision"

ThisBuild / version := "0.1"

ThisBuild / scalaVersion := "2.11.12"

lazy val global = (project in file("."))
  .settings(commonSettings)
  .disablePlugins(AssemblyPlugin)
  .aggregate(
    core,
    emr,
    ec2
  )

  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % "10.1.11"
lazy val core = (project in file("core"))
  .disablePlugins(AssemblyPlugin)
  .settings(
    name := "core",
    commonSettings,
    licenses := apacheLicense,
    libraryDependencies ++= commonDependencies
  )

lazy val emr = (project in file("emr"))
  .settings(
    name := "emr",
    libraryDependencies ++= commonDependencies,
    commonSettings,
    mainClass in assembly := Some("Main"),
  )
  .dependsOn(
    core
  )

lazy val ec2 = (project in file("ec2"))
  .settings(
    name := "ec2",
    commonSettings,
    libraryDependencies ++= commonDependencies ++ Seq(
      dependencies.sttpcore,
      dependencies.sttpcirce,
      dependencies.circegeneric
    ) ++ testDependencies
  )


lazy val dependencies =
  new {
    val circeVersion = "0.11.1"
    val sparkVersion = "2.4.4"
    val sttpVersion = "2.0.0-M1"
    val mleapVersion = "0.15.0"
    val scalacticVersion = "3.1.0"

    val sparkstreaming = "org.apache.spark" %% "spark-streaming" % sparkVersion % "provided"
    val sparkcore = "org.apache.spark" %% "spark-core" % sparkVersion % "provided"
    val sparksql = "org.apache.spark" %% "spark-sql" % sparkVersion % "provided"
    val sparkmllib = "org.apache.spark" %% "spark-mllib" % sparkVersion % "provided"
    val sttpcore = "com.softwaremill.sttp.client" %% "core" % sttpVersion
    val sttpcirce = "com.softwaremill.sttp.client" %% "circe" % sttpVersion
    val mleapspark = "ml.combust.mleap" %% "mleap-spark" % mleapVersion exclude("org.spark-project.spark", "unused")
    val mleapruntime = "ml.combust.mleap" %% "mleap-runtime" % mleapVersion exclude("org.spark-project.spark", "unused")
    val circegeneric = "io.circe" %% "circe-generic" % circeVersion
    val scalactic = "org.scalactic" %% "scalactic" % scalacticVersion
    val scalatest = "org.scalatest" %% "scalatest" % scalacticVersion % Test
  }

lazy val commonDependencies = Seq(
  dependencies.sparkstreaming,
  dependencies.sparkcore,
  dependencies.sparksql,
  dependencies.sparkmllib,
  dependencies.mleapspark,
  dependencies.mleapruntime
)

lazy val testDependencies = Seq(
  dependencies.scalatest,
  dependencies.scalactic
)

lazy val commonSettings = Seq(
  licenses := apacheLicense,
  scalacOptions ++= compilerOptions,
  Compile / scalaSource := baseDirectory.value / "src" / "main" / "scala",
  assemblyJarName in assembly := s"${name.value}-assembly-${version.value}.jar",
  javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
  Test / javaSource := baseDirectory.value / "src" / "test" / "scala",
  test in assembly := {}
)

lazy val compilerOptions = Seq(
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

lazy val apacheLicense = Seq("The Apache Software License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))
