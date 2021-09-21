name := """consuming-tweets"""
organization := "io.github.serdeliverance"

version := "1.0-SNAPSHOT"

scalaVersion := "2.13.6"

lazy val root = project in file(".")

val akkaVersion     = "2.6.16"
val akkaHttpVersion = "10.2.6"
val circeVersion    = "0.12.2"

libraryDependencies ++= Seq(
  "com.google.oauth-client" % "google-oauth-client"                 % "1.18.0-rc",
  "io.circe"                %% "circe-core"                         % circeVersion,
  "io.circe"                %% "circe-parser"                       % circeVersion,
  "io.circe"                %% "circe-generic"                      % circeVersion,
  "io.circe"                %% "circe-generic-extras"               % circeVersion,
  "com.typesafe.akka"       %% "akka-stream"                        % akkaVersion,
  "com.typesafe.akka"       %% "akka-http"                          % akkaHttpVersion,
  "com.lightbend.akka"      %% "akka-stream-alpakka-json-streaming" % "3.0.3",
  // Test
  "com.typesafe.akka"      %% "akka-stream-testkit" % akkaVersion % Test,
  "org.scalatestplus.play" %% "scalatestplus-play"  % "5.0.0"     % Test
)
