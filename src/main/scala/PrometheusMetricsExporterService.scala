package com.github.kovszilard.twitter.server.prometheus

import java.io.StringWriter

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.io.Buf
import com.twitter.util.Future
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.exporter.common.TextFormat

class PrometheusMetricsExporterService(registry: CollectorRegistry = CollectorRegistry.defaultRegistry)
  extends Service[Request, Response] {
  override def apply(request: Request): Future[Response] = {

    val response = Response(request.version, Status.Ok)

    val w = new StringWriter()
    TextFormat.write004(w, registry.metricFamilySamples())
    val content = Buf.Utf8(w.toString)

    response.content = content
    response.headerMap.add("Content-Language", "en")
    response.headerMap.add("Content-Length", content.length.toString)
    response.headerMap.add("Content-Type", TextFormat.CONTENT_TYPE_004)
    Future.value(response)
  }
}