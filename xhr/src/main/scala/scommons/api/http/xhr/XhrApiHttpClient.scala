package scommons.api.http.xhr

import scommons.api.http.ApiHttpData._
import scommons.api.http.xhr.XhrApiHttpClient._
import scommons.api.http.{ApiHttpClient, ApiHttpData, ApiHttpResponse}

import scala.collection.immutable.ArraySeq
import scala.concurrent.duration._
import scala.concurrent.{Future, Promise}
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js
import scala.scalajs.js.typedarray._

class XhrApiHttpClient(baseUrl: String, defaultTimeout: FiniteDuration = 30.seconds)
  extends ApiHttpClient(baseUrl, defaultTimeout) {

  protected[xhr] def execute(method: String,
                             targetUrl: String,
                             params: List[(String, String)],
                             headers: List[(String, String)],
                             data: Option[ApiHttpData],
                             timeout: FiniteDuration): Future[Option[ApiHttpResponse]] = {

    val req = createRequest()
    req.open(method, getFullUrl(targetUrl, params))
    req.timeout = timeout.toMillis.toInt

    val allHeaders = data match {
      case None => headers
      case Some(d) => headers ++ Map("Content-Type" -> d.contentType)
    }

    allHeaders.foreach(x => req.setRequestHeader(x._1, x._2))

    val body = data.map {
      case StringData(d, _) => d
      case UrlEncodedFormData(d) =>
        d.flatMap(item => item._2.map(c => s"${item._1}=${js.URIUtils.encodeURIComponent(c)}")).mkString("&")
    }

    execute(req, body).map {
      case res if res.status == 0 => None //timeout
      case res => Some(new ApiHttpResponse(
        targetUrl,
        res.status,
        parseResponseHeaders(res.getAllResponseHeaders()),
        res.responseText,
        getBodyAsBytes(res.response)
      ))
    }
  }

  private[xhr] def createRequest(): raw.XMLHttpRequest = new raw.XMLHttpRequest()

  private def execute(req: raw.XMLHttpRequest, body: Option[String]): Future[raw.XMLHttpRequest] = {
    val promise = Promise[raw.XMLHttpRequest]()

    req.onreadystatechange = { (_: js.Object) =>
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

object XhrApiHttpClient {

  private val headersLineRegex = """[\r\n]+""".r
  private val headersValueRegex = """: """.r
  
  private def parseResponseHeaders(headers: String): Map[String, Seq[String]] = {
    headersLineRegex.split(headers.trim).map { line =>
      val parts = headersValueRegex.pattern.split(line, 2)
      (parts.head, parts.lastOption.toList)
    }.toMap
  }

  private[xhr] def getBodyAsBytes(response: js.Any): Seq[Byte] = {
    if (response == null || js.isUndefined(response)) Nil
    else {
      //TODO: handle Blob response as well
      val resArr = new Int8Array(response.asInstanceOf[ArrayBuffer]).toArray
      ArraySeq.unsafeWrapArray(resArr)
    }
  }
  
  private[xhr] def getFullUrl(url: String, params: List[(String, String)]): String = {

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
