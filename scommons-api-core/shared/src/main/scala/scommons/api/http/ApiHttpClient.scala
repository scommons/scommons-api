package scommons.api.http

import play.api.libs.json._
import scommons.api.http.ApiHttpClient._

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

abstract class ApiHttpClient(baseUrl: String,
                             defaultTimeout: FiniteDuration = 30.seconds)(implicit ec: ExecutionContext) {

  def execGet[R](url: String,
                 params: List[(String, String)] = Nil,
                 timeout: FiniteDuration = defaultTimeout
                )(implicit jsonReads: Reads[R]): Future[R] = {

    exec[String, R]("GET", url, params, None, timeout)
  }

  def execPost[D, R](url: String,
                     data: D,
                     params: List[(String, String)] = Nil,
                     timeout: FiniteDuration = defaultTimeout
                    )(implicit jsonWrites: Writes[D], jsonReads: Reads[R]): Future[R] = {

    exec("POST", url, params, Some(data), timeout)
  }

  def execPut[D, R](url: String,
                    data: D,
                    params: List[(String, String)] = Nil,
                    timeout: FiniteDuration = defaultTimeout
                   )(implicit jsonWrites: Writes[D], jsonReads: Reads[R]): Future[R] = {

    exec("PUT", url, params, Some(data), timeout)
  }

  def execDelete[D, R](url: String,
                       data: Option[D] = None,
                       params: List[(String, String)] = Nil,
                       timeout: FiniteDuration = defaultTimeout
                      )(implicit jsonWrites: Writes[D], jsonReads: Reads[R]): Future[R] = {

    exec("DELETE", url, params, data, timeout)
  }

  private def exec[T, R](method: String,
                         url: String,
                         params: List[(String, String)],
                         data: Option[T],
                         timeout: FiniteDuration
                        )(implicit jsonWrites: Writes[T], jsonReads: Reads[R]): Future[R] = {

    val targetUrl = getTargetUrl(baseUrl, url)

    execute(
      method,
      targetUrl,
      params,
      data.map { d =>
        Json.stringify(Json.toJson(d))
      },
      timeout
    ).map(parseResponse(targetUrl, _))
  }

  protected def execute(method: String,
                        targetUrl: String,
                        params: List[(String, String)],
                        jsonBody: Option[String],
                        timeout: FiniteDuration
                       ): Future[Option[ApiHttpResponse]]

  private[http] def parseResponse[R](url: String, response: Option[ApiHttpResponse])
                                    (implicit jsonReads: Reads[R]): R = response match {

    case None =>
      throw new Exception(
        s"""Request timed out, unable to get timely response for:
           |$url""".stripMargin)

    case Some(res) if res.status <= 299 =>
      val body = res.body
      Json.parse(body).validate[R] match {
        case JsSuccess(data, _) => data
        case JsError(error) =>
          val err =
            s"""Error parsing http response:
               |url: $url
               |status: ${res.status}
               |error: $error
               |body: $body""".stripMargin
          throw new Exception(err)
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
        case None =>
          throw new Exception(
            s"""Received error response:
               |url: $url
               |status: ${other.status}
               |body: $body""".stripMargin)
      }
  }
}

object ApiHttpClient {

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
