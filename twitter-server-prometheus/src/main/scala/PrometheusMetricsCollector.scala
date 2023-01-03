package com.twitter.finagle.stats

import com.github.kovszilard.twitter.server.prometheus.PrometheusMetricsCodec
import io.prometheus.client.Collector
import io.prometheus.client.Collector.MetricFamilySamples.Sample
import io.prometheus.client.Collector._

import scala.jdk.CollectionConverters._
import scala.util.Try


case class PrometheusMetricsCollector(codec: PrometheusMetricsCodec, registry: Metrics) extends Collector {

  implicit def listConverter[A](list: List[A]): java.util.List[A] = list.asJava
  implicit def mapConverter[K, V](map: java.util.Map[K, V]): scala.collection.mutable.Map[K, V] = map.asScala

  override def collect(): java.util.List[MetricFamilySamples] = {
    val gauges = registry.gauges.map { gaugeSnapshot =>
      fromGauge(gaugeSnapshot.hierarchicalName, gaugeSnapshot.value)
    }
    val counters = registry.counters.map { counterSnapshot =>
      fromCounter(counterSnapshot.hierarchicalName, counterSnapshot.value)
    }
    val histograms = registry.histograms.map{ histogramSnapshot =>
      fromHistogram(histogramSnapshot.hierarchicalName, histogramSnapshot.value)
    }

    (gauges ++ counters ++ histograms).toList
  }

  def extractAndSanitizeMetricAndLabels(rawName: String): (String, List[(String, String)]) = {
    val (name, labels) = Try {
      codec.fromMetricName(rawName)
    }.getOrElse{
      // TODO Fix potential high cardinality if dynamic non-envodable values end up as the rawName
      rawName -> List("finagleName" -> rawName)
    }
    (sanitizeMetricName(name), labels)
  }

  def fromGauge(name: String, value: Number): MetricFamilySamples = {
    val (metric, labels) = extractAndSanitizeMetricAndLabels(name)

    new MetricFamilySamples(
      metric,
      Type.GAUGE,
      genHelpMessage(name, "gauge"),
      List(new Sample(metric, labels.map(_._1), labels.map(_._2), value.doubleValue()))
    )
  }

  def fromCounter(name: String, value: Number): MetricFamilySamples = {
    val (metric, labels) = extractAndSanitizeMetricAndLabels(name)

    new MetricFamilySamples(
      metric,
      Type.COUNTER,
      genHelpMessage(name, "counter"),
      List(new Sample(metric, labels.map(_._1), labels.map(_._2), value.doubleValue()))
    )
  }

  def fromHistogram(name: String, snapshot: Snapshot): MetricFamilySamples = {
    val (metric, labels) = extractAndSanitizeMetricAndLabels(name)
    val labelNames = labels.map(_._1)
    val labelValues = labels.map(_._2)

    val count = new Sample(s"${metric}_count", labelNames, labelValues, snapshot.count.toDouble)
    val sum = new Sample(s"${metric}_sum", labelNames, labelValues, snapshot.sum.toDouble)
    val max = new Sample(s"${metric}_max", labelNames, labelValues, snapshot.max.toDouble)
    val min = new Sample(s"${metric}_min", labelNames, labelValues, snapshot.min.toDouble)
    val avg = new Sample(s"${metric}_avg", labelNames, labelValues, snapshot.average)

    val percentiles = snapshot.percentiles.map { percentile =>
      new Sample(metric, labelNames :+ "quantile", labelValues :+ percentile.quantile.toString, percentile.value.toDouble)
    }

    new MetricFamilySamples(
      metric,
      Type.SUMMARY,
      genHelpMessage(name, "histogram"),
      List(count, sum, max, min, avg) ++ percentiles
    )
  }

  def genHelpMessage(name: String, originalType: String): String = {
    s"Generated from finagle metric (name=$name, type=$originalType)"
  }

}
