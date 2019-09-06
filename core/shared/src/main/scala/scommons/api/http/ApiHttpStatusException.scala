package scommons.api.http

import scommons.api.http.ApiHttpStatusException._

case class ApiHttpStatusException(error: String,
                                  url: String,
                                  status: Int,
                                  body: String
                                 ) extends RuntimeException(buildMessage(error, url, status, body))

object ApiHttpStatusException {

  def buildMessage(error: String,
                   url: String,
                   status: Int,
                   body: String): String = {
    
    s"""$error
       |  url: $url
       |  status: $status
       |  body: $body""".stripMargin
  }
}
