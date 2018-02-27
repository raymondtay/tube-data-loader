import Dependencies.generalLibs

name := "tube-data-loader"

description := "Serving simulated data to Tube for the purpose of performance testing"

scalaVersion := "2.12.4"

libraryDependencies ++= generalLibs

initialCommands in console := """
import tube.dataloader.core.generator.Generators._
val team = genTeam.sample.get

val users = genUsers(team).sample.get

val channels = genChannels(users).sample.get

import io.circe._, syntax._, tube.dataloader.codec.JsonCodec._

channels.asJson
"""
