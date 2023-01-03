import sbt._

object Dependencies {
  val twitterVersion = "22.12.0"
  
  lazy val twitterServer = "com.twitter" %% "twitter-server" % twitterVersion
  lazy val finagleStats = "com.twitter" %% "finagle-stats" % twitterVersion
  lazy val prometheusSimpleClient = "io.prometheus" % "simpleclient" % "0.16.0"
  lazy val prometheusSimpleClientCommon = "io.prometheus" % "simpleclient_common" % "0.16.0"
  
  lazy val twitterServerLogback = "com.twitter" %% "twitter-server-logback-classic" % twitterVersion
  lazy val logback = "ch.qos.logback" % "logback-classic" % "1.4.5"
  
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5"
}
