import sbt._

object Dependencies {

  val catsVersion  = "1.0.1"
  val circeVersion = "0.9.1"
  val akkaVersion = "2.5.10"
  val akkaHttpVersion = "10.1.0-RC2"
  val scalaCheckVersion = "1.13.4"
  val shapelessVersion = "2.3.3"

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

  val catsLib =
    Seq("org.typelevel" %% "cats-core" % catsVersion,
        "org.typelevel" %% "cats-free" % catsVersion)

  val shapelessLib = Seq("com.chuusai" %% "shapeless" % shapelessVersion)

  val scalaCheckLib = Seq("org.scalacheck" %% "scalacheck" % scalaCheckVersion) // for random data generation

  val generalLibs = coreLibs ++ akkaLibs ++ scalaCheckLib ++ catsLib ++ shapelessLib

}
