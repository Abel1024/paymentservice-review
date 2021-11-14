ThisBuild / organization := "S.O.L.I.D. IT"
ThisBuild / scalaVersion := "2.13.7"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / Test / parallelExecution := true

mainClass / run := Some("http.HttpServer")

val tapirVersion = "0.19.0-M16"
val catsVersion = "3.2.9"

lazy val commonSettings = Seq(
  libraryDependencies ++= Seq(
    "org.slf4j" % "slf4j-simple" % "1.7.32",
    "org.scalatest" %% "scalatest" % "3.2.10" % "test"
  )
)

lazy val `ps-time` = project
  .settings(commonSettings)

lazy val `ps-domain` = project
  .settings(commonSettings)
  .dependsOn(`ps-time`, `ps-transfer`)

lazy val `ps-service` = project
  .settings(commonSettings)
  .dependsOn(`ps-domain`)
  .settings(libraryDependencies ++= Seq(
    "com.github.pureconfig" %% "pureconfig" % "0.17.0"
  ))

lazy val `ps-transfer` = project

lazy val `ps-root` = (project in file("."))
  .aggregate(`ps-time`, `ps-domain`, `ps-service`)
  .dependsOn(`ps-service`, `ps-transfer`)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.tapir" %% "tapir-akka-http-server" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-json-spray" % tapirVersion,
      "org.typelevel" %% "cats-effect" % catsVersion
    )
  )

