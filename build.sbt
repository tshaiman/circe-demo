name := "circeDemo"

version := "0.1"

scalaVersion := "2.12.8"

val circeVersion = "0.10.0"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "2.22.0"
