import Dependencies._

ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := twitterVersion
ThisBuild / organization     := "com.github"
ThisBuild / organizationName := "kovszilard"

lazy val root = (project in file("."))
  .aggregate(twitterServerPrometheus, example)

lazy val twitterServerPrometheus = (project in file("twitter-server-prometheus"))
    .settings(
        name := "twitter-server-prometheus",
        crossScalaVersions := Seq("2.12.8", "2.11.12"),
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
        crossScalaVersions := Seq("2.12.8", "2.11.12"),
        libraryDependencies ++= Seq(
            twitterServerLogback,
            logback
        )
    ).dependsOn(twitterServerPrometheus)
