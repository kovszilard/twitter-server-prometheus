ThisBuild / description := "Prometheus metrics for Twitter Server."
ThisBuild / homepage := Some(url("https://github.com/kovszilard/twitter-server-prometheus"))
ThisBuild / organizationHomepage := Some(url("https://github.com/kovszilard/"))
ThisBuild / licenses := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/kovszilard/twitter-server-prometheus"),
    "scm:git@github.com:kovszilard/twitter-server-prometheus.git"
  )
)

ThisBuild / developers := List(
  Developer(
    id    = "kovszilard",
    name  = "Szilard Kovacs",
    email = "",
    url   = url("https://github.com/kovszilard/")
  )
)

// Remove all additional repository other than Maven Central from POM
ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
ThisBuild / publishMavenStyle := true
