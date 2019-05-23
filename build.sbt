import Dependencies._

ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := twitterVersion
ThisBuild / organization     := "com.github"
ThisBuild / organizationName := "kovszilard"

lazy val twitterServerPrometheus = (project in file("."))
  .settings(
    name := "twitter-server-prometheus",
    crossScalaVersions := Seq("2.12.8", "2.11.12"),
    libraryDependencies ++= Seq(
        twitterServer,
        finagleStats,
        twitterServerLogback,
        logback,
        prometheusSimpleClient,
        prometheusSimpleClientCommon
        )
  )
