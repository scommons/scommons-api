package scommons.api.http

import scommons.api.http.ApiHttpStatusException._

case class ApiHttpStatusException(error: String,
                                  resp: ApiHttpResponse
                                 ) extends RuntimeException(buildMessage(error, resp.url, resp.status, resp.body))

object ApiHttpStatusException {

  def buildMessage(error: String,
                   url: String,
                   status: Int,
                   body: String): String = {
    
    def printBody: String = {
      val maxLen = 1024
      if (body.length > maxLen) s"${body.take(maxLen)}..."
      else body
    }
    
    s"""$error
       |  url: $url
       |  status: $status
       |  body: $printBody""".stripMargin
  }
}
