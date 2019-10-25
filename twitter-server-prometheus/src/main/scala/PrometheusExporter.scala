package com.github.kovszilard.twitter.server.prometheus

import com.twitter.app.App
import com.twitter.finagle.stats.PrometheusMetricsCollector
import com.twitter.server.Admin.Grouping
import com.twitter.server.AdminHttpServer.Route
import com.twitter.server.{AdminHttpServer, Stats}

trait PrometheusExporter { self: App with AdminHttpServer with Stats =>

  PrometheusMetricsCollector().register()

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
