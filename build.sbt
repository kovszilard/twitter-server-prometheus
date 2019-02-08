name := "twitter-server-prometheus"

version := "0.1"

scalaVersion := "2.11.12"

crossScalaVersions := Seq("2.12.8", "2.11.12")

val twitterVersion = "19.1.0"

libraryDependencies += "com.twitter" %% "twitter-server" % twitterVersion
libraryDependencies += "com.twitter" %% "finagle-stats" % twitterVersion

//libraryDependencies += "com.twitter" %% "twitter-server-logback-classic" % twitterVersion
//libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"

libraryDependencies += "io.prometheus" % "simpleclient" % "0.6.0"
libraryDependencies += "io.prometheus" % "simpleclient_common" % "0.6.0"
