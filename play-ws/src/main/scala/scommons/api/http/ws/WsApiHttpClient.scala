package scommons.api.http.ws

import java.util.concurrent.TimeoutException

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.ByteString
import play.api.libs.ws.DefaultBodyWritables._
import play.api.libs.ws.ahc.StandaloneAhcWSClient
import play.api.libs.ws.{BodyWritable, InMemoryBody, StandaloneWSRequest, StandaloneWSResponse}
import scommons.api.http.ApiHttpData._
import scommons.api.http.{ApiHttpClient, ApiHttpData, ApiHttpResponse}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class WsApiHttpClient(baseUrl: String,
                      defaultTimeout: FiniteDuration = 30.seconds)
                     (implicit system: ActorSystem)
  extends ApiHttpClient(baseUrl, defaultTimeout)(system.dispatcher) {

  private implicit val ec: ExecutionContext = system.dispatcher
  private implicit val materializer: Materializer = ActorMaterializer()

  private[ws] val ws = StandaloneAhcWSClient()

  system.registerOnTermination {
    ws.close()
  }

  protected[ws] def execute(method: String,
                            targetUrl: String,
                            params: List[(String, String)],
                            headers: List[(String, String)],
                            data: Option[ApiHttpData],
                            timeout: FiniteDuration): Future[Option[ApiHttpResponse]] = {

    val req: StandaloneWSRequest = data match {
      case None => ws.url(targetUrl)
      case Some(StringData(body, contentType)) =>
        ws.url(targetUrl).withBody(body)(WsApiHttpClient.writeableOfString(contentType))
      case Some(UrlEncodedFormData(body)) =>
        ws.url(targetUrl).withBody(body)
    }

    execute(req.withMethod(method)
      .withFollowRedirects(true)
      .withQueryStringParameters(params: _*)
      .withHttpHeaders(headers: _*)
      .withRequestTimeout(timeout)
    ).map { resp =>
      Some(ApiHttpResponse(targetUrl, resp.status, resp.headers, resp.body))
    }.recover {
      case _: TimeoutException => None
    }
  }

  private[ws] def execute(req: StandaloneWSRequest): Future[StandaloneWSResponse] = {
    req.execute()
  }
}

object WsApiHttpClient {

  private def writeableOfString(contentType: String): BodyWritable[String] = {
    BodyWritable(str => InMemoryBody(ByteString.fromString(str)), contentType)
  }
}
