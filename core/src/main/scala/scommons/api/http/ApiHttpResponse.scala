package scommons.api.http

import scala.collection.immutable.ArraySeq

class ApiHttpResponse(val url: String,
                      val status: Int,
                      val headers: Map[String, scala.collection.Seq[String]],
                      getBody: => String,
                      getBodyAsBytes: => Seq[Byte]) {

  private lazy val _body: String = getBody
  private lazy val _bodyAsBytes: Seq[Byte] = getBodyAsBytes
  
  def body: String = _body
  def bodyAsBytes: Seq[Byte] = _bodyAsBytes
}

object ApiHttpResponse {

  def apply(url: String,
            status: Int,
            headers: Map[String, scala.collection.Seq[String]],
            body: String): ApiHttpResponse = {
    
    new ApiHttpResponse(url, status, headers, body, ArraySeq.unsafeWrapArray(body.getBytes))
  }
}
