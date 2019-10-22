package scommons.api.http

import play.api.libs.json._
import scommons.api.http.ApiHttpClient._
import scommons.api.http.ApiHttpMethod._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

abstract class ApiHttpClient(baseUrl: String,
                             defaultTimeout: FiniteDuration = 30.seconds
                            )(implicit ec: ExecutionContext) {

  def execGet[R](url: String,
                 params: List[(String, String)] = Nil,
                 headers: List[(String, String)] = Nil,
                 timeout: FiniteDuration = defaultTimeout
                )(implicit jsonReads: Reads[R]): Future[R] = {

    exec[String](GET, url, params, headers, None, timeout).map(parseResponse[R])
  }

  def execPost[D, R](url: String,
                     data: D,
                     params: List[(String, String)] = Nil,
                     headers: List[(String, String)] = Nil,
                     timeout: FiniteDuration = defaultTimeout
                    )(implicit jsonWrites: Writes[D], jsonReads: Reads[R]): Future[R] = {

    exec(POST, url, params, headers, Some(data), timeout).map(parseResponse[R])
  }

  def execPut[D, R](url: String,
                    data: D,
                    params: List[(String, String)] = Nil,
                    headers: List[(String, String)] = Nil,
                    timeout: FiniteDuration = defaultTimeout
                   )(implicit jsonWrites: Writes[D], jsonReads: Reads[R]): Future[R] = {

    exec(PUT, url, params, headers, Some(data), timeout).map(parseResponse[R])
  }

  def execDelete[D, R](url: String,
                       data: Option[D] = None,
                       params: List[(String, String)] = Nil,
                       headers: List[(String, String)] = Nil,
                       timeout: FiniteDuration = defaultTimeout
                      )(implicit jsonWrites: Writes[D], jsonReads: Reads[R]): Future[R] = {

    exec(DELETE, url, params, headers, data, timeout).map(parseResponse[R])
  }

  private def exec[T](method: ApiHttpMethod,
                      url: String,
                      params: List[(String, String)],
                      headers: List[(String, String)],
                      data: Option[T],
                      timeout: FiniteDuration
                     )(implicit jsonWrites: Writes[T]): Future[ApiHttpResponse] = {

    val targetUrl = getTargetUrl(baseUrl, url)

    execute(
      method = method.toString,
      targetUrl = targetUrl,
      params = params,
      headers = headers,
      jsonBody = data.map { d =>
        Json.stringify(Json.toJson(d))
      },
      timeout = timeout
    ).map {
      case None => throw ApiHttpTimeoutException(targetUrl)
      case Some(resp) => resp
    }
  }

  protected def execute(method: String,
                        targetUrl: String,
                        params: List[(String, String)],
                        headers: List[(String, String)],
                        jsonBody: Option[String],
                        timeout: FiniteDuration
                       ): Future[Option[ApiHttpResponse]]
}

object ApiHttpClient {

  def parseResponse[R](resp: ApiHttpResponse)(implicit jsonReads: Reads[R]): R = {
    val body = resp.body

    if (resp.status <= 299) {
      Json.parse(body).validate[R] match {
        case JsSuccess(data, _) => data
        case JsError(error) =>
          throw ApiHttpStatusException(s"Fail to parse http response, error: $error", resp)
      }
    }
    else {
      val maybeData =
        if (body.trim.startsWith("{")) {
          Json.parse(body).validate[R] match {
            case JsSuccess(data, _) => Some(data)
            case _ => None
          }
        }
        else None

      maybeData match {
        case Some(data) => data
        case None => throw ApiHttpStatusException("Received error response", resp)
      }
    }
  }

  def queryParams(params: (String, Option[_])*): List[(String, String)] = params.collect {
    case (p, Some(v)) => (p, v.toString)
  }.toList

  private[http] def getTargetUrl(baseUrl: String, url: String): String = {
    val normalizedUrl =
      if (url.startsWith("/")) url.substring(1)
      else url

    if (baseUrl.endsWith("/"))
      s"$baseUrl$normalizedUrl"
    else
      s"$baseUrl/$normalizedUrl"
  }
}
