package com.twitter.finagle.stats

import io.prometheus.client.Collector
import io.prometheus.client.Collector.MetricFamilySamples.Sample
import io.prometheus.client.Collector._

import scala.jdk.CollectionConverters._


class PrometheusMetricsCollector(registry: MetricsView = MetricsStatsReceiver.defaultRegistry) extends Collector {

  implicit def listConverter[A](list: List[A]): java.util.List[A] = list.asJava
  implicit def mapConverter[K, V](map: java.util.Map[K, V]): scala.collection.mutable.Map[K, V] = map.asScala

  override def collect(): java.util.List[MetricFamilySamples] = {
    val gauges = registry.gauges.map{ case (name: String, value: Number) => fromGauge(name, value)}
    val counters = registry.counters.map{ case (name: String, value: Number) => fromCounter(name, value)}
    val histograms = registry.histograms.map{ case (name: String, value: Snapshot) => fromHistogram(name, value)}
    (gauges ++ counters ++ histograms).toList
  }

  def fromGauge(name: String, value: Number): MetricFamilySamples = {
    val prometheusName = sanitizeMetricName(name)

    new MetricFamilySamples(
      prometheusName,
      Type.GAUGE,
      genHelpMessage(name, "gauge"),
      List(new Sample(prometheusName, List("finagleName"), List(name), value.doubleValue()))
    )
  }

  def fromCounter(name: String, value: Number): MetricFamilySamples = {
    val prometheusName = sanitizeMetricName(name)

    new MetricFamilySamples(
      prometheusName,
      Type.COUNTER,
      genHelpMessage(name, "counter"),
      List(new Sample(prometheusName, List("finagleName"), List(name), value.doubleValue()))
    )
  }

  def fromHistogram(name: String, snapshot: Snapshot): MetricFamilySamples = {
    val prometheusName = sanitizeMetricName(name)

    val count = new Sample(s"${prometheusName}_count", Nil, Nil, snapshot.count.toDouble)
    val sum = new Sample(s"${prometheusName}_sum", Nil, Nil, snapshot.sum.toDouble)
    val max = new Sample(s"${prometheusName}_max", Nil, Nil, snapshot.max.toDouble)
    val min = new Sample(s"${prometheusName}_min", Nil, Nil, snapshot.min.toDouble)
    val avg = new Sample(s"${prometheusName}_avg", Nil, Nil, snapshot.average)

    val percentiles = snapshot.percentiles.map{percentile =>
      new Sample(prometheusName, List("quantile"), List(percentile.quantile.toString), percentile.value.toDouble)
    }

    new MetricFamilySamples(
      prometheusName,
      Type.SUMMARY,
      genHelpMessage(name, "histogram"),
      List(count, sum, max, min, avg) ++ percentiles
    )
  }

  def genHelpMessage(name: String, originalType: String): String = {
    s"Generated from finagle metric (name=$name, type=$originalType)"
  }

}

object PrometheusMetricsCollector {
  def apply() = new PrometheusMetricsCollector()
  def apply(registry: Metrics) = new PrometheusMetricsCollector(registry)
}
