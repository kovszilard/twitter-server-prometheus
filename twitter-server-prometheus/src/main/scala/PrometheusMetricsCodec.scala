package com.github.kovszilard.twitter.server.prometheus

trait PrometheusMetricsCodec {
  def fromMetricName(metricName: String): (String, List[(String, String)]) = (metricName, List.empty)
  def toMetricName(name: String, metadata: List[(String, String)]): String = name
}
