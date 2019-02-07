package kovszilard.twitter.server.prometheus

import com.twitter.app.App
import com.twitter.finagle.stats.FinagleMetricsCollector
import com.twitter.server.Admin.Grouping
import com.twitter.server.AdminHttpServer.Route
import com.twitter.server.{Admin, AdminHttpServer, Stats}

trait PrometheusExporter extends Admin { self: App with AdminHttpServer with Stats =>

  FinagleMetricsCollector().register()

  override protected def routes: Seq[Route] = {
    super.routes ++ Seq(Route.isolate(Route(
      path = "/metrics",
      handler = new PrometheusMetricsExporterService(),
      alias = "Prometheus Metrics",
      group = Some(Grouping.Metrics),
      includeInIndex = true
    )))
  }
}
