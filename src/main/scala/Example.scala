package kovszilard.twitter.server.prometheus

import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.finagle.stats.LoadedStatsReceiver
import com.twitter.server.TwitterServer
import com.twitter.util.{Await, Future}

object Example extends TwitterServer with PrometheusExporter {

  val receiver = LoadedStatsReceiver.scope("prometheus_demo")
  val requests = receiver.counter("http_requests")

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
