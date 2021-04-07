package com.github.kovszilard.twitter.server.prometheus

import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.finagle.stats.LoadedStatsReceiver
import com.twitter.server.TwitterServer
import com.twitter.util.{Await, Future}

object Example extends TwitterServer with PrometheusExporter {

  val receiver = LoadedStatsReceiver.scope("prometheus_demo")
  override val metricsCodec: PrometheusMetricsCodec = new PrometheusMetricsCodec {
    override def fromMetricName(metricName: String): (String, List[(String, String)]) = {
      val name :: params = metricName.split('?').toList
      val labels = params.map(_.split('&').flatMap(_.split("=").toList).toList).flatMap {
        case Nil =>
          None
        case name :: Nil =>
          Some(name -> "")
        case name :: value :: _ =>
          Some(name -> value)
      }
      (name, labels)
    }
    override def toMetricName(name: String, metadata: List[(String, String)]): String = {
      val params: List[String] = metadata.map { case (name, value) => s"$name=$value" }
      name + params.mkString("?", "&", "")
    }
  }


  val requests = receiver.counter(metricsCodec.toMetricName("http_requests", List("id" -> "4")))


  val helloWorldService = new Service[Request, Response] {
    def apply(request: Request): Future[Response] = {
      requests.incr()
      val response = Response(request.version, Status.Ok)
      response.contentString = "Hello World!"
      Future.value(response)
    }
  }

  def main() = {
    val server = Http.serve(":8080", helloWorldService)

    closeOnExit(server)

    Await.ready(adminHttpServer)
  }
}
