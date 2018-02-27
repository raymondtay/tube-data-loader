import sbt._

object Dependencies {

  val circeVersion = "0.9.1"
  val akkaVersion = "2.5.10"
  val akkaHttpVersion = "10.1.0-RC2"
  val scalaCheckVersion = "1.13.4"

  val akkaLibs = Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion
  )

  val coreLibs = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
  ).map(_ % circeVersion)

  val scalaCheckLib = Seq("org.scalacheck" %% "scalacheck" % scalaCheckVersion) // for random data generation

  val generalLibs = coreLibs ++ akkaLibs ++ scalaCheckLib

}
