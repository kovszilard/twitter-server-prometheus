# Prometheus metrics for Twitter Server
[![Build Status](https://travis-ci.org/kovszilard/twitter-server-prometheus.svg?branch=master)](https://travis-ci.org/kovszilard/twitter-server-prometheus)
[![Maven central](https://img.shields.io/maven-central/v/com.github.kovszilard/twitter-server-prometheus_2.12)](https://search.maven.org/search?q=twitter-server-prometheus)
[![Tweet](https://img.shields.io/twitter/url?style=social&url=https%3A%2F%2Fgithub.com%2Fkovszilard%2Fsmenu)](https://twitter.com/intent/tweet?text=Wow:&url=https%3A%2F%2Fgithub.com%2Fkovszilard%2Fsmenu)
[![Twitter follow](https://img.shields.io/twitter/follow/kovszilard?style=social)](https://twitter.com/intent/follow?screen_name=kovszilard)

Metrics exporter for twitter server, exposing prometheus metrics.

Based on the blog post and code from [Footballradar](https://engineering.footballradar.com/prometheus-at-football-radar/)

## Features

* Integrated to the admin UI ![Screenshot](Screenshot.png)
* Metrics are exposed on the admin port `/metrics` http endpoint.
* The service is detached from the main thread pool of the rest of the app just like the `/admin/metrics.json`.
* Exported histograms include `count`, `sum`, `min`, `max`, `avg` and `quantile` data just like in Finagle

## Usage

Include it in your project by adding the following to your build.sbt:

```scala
libraryDependencies += "com.github.kovszilard" %% "twitter-server-prometheus" % "20.10.0"
```

Once you have the SBT dependency, you can mix in the `PrometheusExporter` trait to your App.

```scala
object MyApp extends TwitterServer with PrometheusExporter {
  // ...
}
```

## Example

See Example.scala

and run it with:

```
sbt example/runMain com.github.kovszilard.twitter.server.prometheus.Example
```
