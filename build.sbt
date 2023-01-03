import Dependencies._

ThisBuild / scalaVersion     := "2.13.10"
ThisBuild / version          := twitterVersion
ThisBuild / organization     := "com.github.kovszilard"
ThisBuild / organizationName := "kovszilard"

useGpg := true
useGpgAgent := true
useGpgPinentry := true

lazy val root = (project in file("."))
  .aggregate(twitterServerPrometheus, example)
  .settings(
    skip in publish := true
  )

lazy val twitterServerPrometheus = (project in file("twitter-server-prometheus"))
    .settings(
        name := "twitter-server-prometheus",
        crossScalaVersions := Seq("2.13.10", "2.12.10", "2.11.12"),
        libraryDependencies ++= Seq(
            twitterServer,
            finagleStats,
            prometheusSimpleClient,
            prometheusSimpleClientCommon
        )
  )

lazy val example = (project in file("example"))
    .settings(
        name := "example",
        crossScalaVersions := Seq("2.13.10", "2.12.10", "2.11.12"),
        libraryDependencies ++= Seq(
            twitterServerLogback,
            logback
        ),
        skip in publish := true
    ).dependsOn(twitterServerPrometheus)
