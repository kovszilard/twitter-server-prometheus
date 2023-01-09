package com.github.kovszilard.twitter.server.prometheus

import com.twitter.app.App
import com.twitter.finagle.stats.{MetricsStatsReceiver, PrometheusMetricsCollector}
import com.twitter.server.Admin.Grouping
import com.twitter.server.AdminHttpServer.Route
import com.twitter.server.{AdminHttpServer, Stats}

trait PrometheusExporter { self: App with AdminHttpServer with Stats =>

  lazy val metricsCodec: PrometheusMetricsCodec = new PrometheusMetricsCodec {}
  lazy val metricsRegistry = MetricsStatsReceiver.defaultRegistry

  PrometheusMetricsCollector(metricsCodec, metricsRegistry).register()

  val metricsRoute: Route = Route.isolate(Route(
      path = "/metrics",
      handler = new PrometheusMetricsExporterService(),
      alias = "Prometheus Metrics",
      group = Some(Grouping.Metrics),
      includeInIndex = true
    )
  )

  addAdminRoute(metricsRoute)
}
