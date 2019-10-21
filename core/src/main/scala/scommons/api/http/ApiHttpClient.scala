package scommons.api.http

import play.api.libs.json._
import scommons.api.http.ApiHttpClient._

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

abstract class ApiHttpClient(baseUrl: String,
                             defaultTimeout: FiniteDuration = 30.seconds)(implicit ec: ExecutionContext) {

  def execGet[R](url: String,
                 params: List[(String, String)] = Nil,
                 headers: List[(String, String)] = Nil,
                 timeout: FiniteDuration = defaultTimeout
                )(implicit jsonReads: Reads[R]): Future[R] = {

    exec[String, R]("GET", url, params, headers, None, timeout)
  }

  def execPost[D, R](url: String,
                     data: D,
                     params: List[(String, String)] = Nil,
                     headers: List[(String, String)] = Nil,
                     timeout: FiniteDuration = defaultTimeout
                    )(implicit jsonWrites: Writes[D], jsonReads: Reads[R]): Future[R] = {

    exec("POST", url, params, headers, Some(data), timeout)
  }

  def execPut[D, R](url: String,
                    data: D,
                    params: List[(String, String)] = Nil,
                    headers: List[(String, String)] = Nil,
                    timeout: FiniteDuration = defaultTimeout
                   )(implicit jsonWrites: Writes[D], jsonReads: Reads[R]): Future[R] = {

    exec("PUT", url, params, headers, Some(data), timeout)
  }

  def execDelete[D, R](url: String,
                       data: Option[D] = None,
                       params: List[(String, String)] = Nil,
                       headers: List[(String, String)] = Nil,
                       timeout: FiniteDuration = defaultTimeout
                      )(implicit jsonWrites: Writes[D], jsonReads: Reads[R]): Future[R] = {

    exec("DELETE", url, params, headers, data, timeout)
  }

  private def exec[T, R](method: String,
                         url: String,
                         params: List[(String, String)],
                         headers: List[(String, String)],
                         data: Option[T],
                         timeout: FiniteDuration
                        )(implicit jsonWrites: Writes[T], jsonReads: Reads[R]): Future[R] = {

    val targetUrl = getTargetUrl(baseUrl, url)

    execute(
      method = method,
      targetUrl = targetUrl,
      params = params,
      headers = headers,
      jsonBody = data.map { d =>
        Json.stringify(Json.toJson(d))
      },
      timeout = timeout
    ).map(parseResponse[R](targetUrl, _))
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

  def parseResponse[R](url: String, response: Option[ApiHttpResponse])
                      (implicit jsonReads: Reads[R]): R = response match {

    case None => throw ApiHttpTimeoutException(url)

    case Some(res) if res.status <= 299 =>
      val body = res.body
      Json.parse(body).validate[R] match {
        case JsSuccess(data, _) => data
        case JsError(error) =>
          throw ApiHttpStatusException(s"Fail to parse http response, error: $error", res)
      }

    case Some(other) =>
      val body = other.body
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
        case None => throw ApiHttpStatusException("Received error response", other)
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
