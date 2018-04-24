package scommons.api.http.js

import org.scalajs.dom
import scommons.api.http.{ApiHttpClient, ApiHttpResponse}

import scala.concurrent.duration._
import scala.concurrent.{Future, Promise}
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js

class JsApiHttpClient(baseUrl: String, defaultTimeout: FiniteDuration = 30.seconds)
  extends ApiHttpClient(baseUrl, defaultTimeout) {

  protected[js] def execute(method: String,
                            targetUrl: String,
                            params: List[(String, String)],
                            jsonBody: Option[String],
                            timeout: FiniteDuration): Future[Option[ApiHttpResponse]] = {

    val req = createRequest()
    req.open(method, JsApiHttpClient.getFullUrl(targetUrl, params))
    req.timeout = timeout.toMillis.toInt

    val headers =
      if (jsonBody.isDefined) Map("Content-Type" -> "application/json")
      else Map.empty[String, String]

    headers.foreach(x => req.setRequestHeader(x._1, x._2))

    execute(req, jsonBody).map {
      case res if res.status == 0 => None //timeout
      case res => Some(ApiHttpResponse(res.status, res.responseText))
    }
  }

  private[js] def createRequest(): dom.XMLHttpRequest = new dom.XMLHttpRequest()

  private[js] def execute(req: dom.XMLHttpRequest, body: Option[String]): Future[dom.XMLHttpRequest] = {
    val promise = Promise[dom.XMLHttpRequest]()

    req.onreadystatechange = { (_: dom.Event) =>
      if (req.readyState == 4) {
        promise.success(req)
      }
    }

    body match {
      case None => req.send()
      case Some(data) => req.send(data)
    }

    promise.future
  }
}

object JsApiHttpClient {

  private[js] def getFullUrl(url: String, params: List[(String, String)]): String = {

    def enc(p: String) = js.URIUtils.encodeURIComponent(p)

    val queryString = params.foldLeft(Map.empty[String, Seq[String]]) {
      case (m, (k, v)) => m + (k -> (v +: m.getOrElse(k, Nil)))
    }

    if (queryString.isEmpty) url
    else {
      val qs = (for {
        (n, vs) <- queryString
        v <- vs
      } yield {
        s"${enc(n)}=${enc(v)}"
      }).mkString("&")

      s"$url?$qs"
    }
  }
}
