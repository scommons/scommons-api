package scommons.api.http

sealed trait ApiHttpData {
  
  def contentType: String
}

object ApiHttpData {

  case class StringData(data: String,
                        contentType: String = "application/json"
                       ) extends ApiHttpData
  
  case class UrlEncodedFormData(data: Map[String, Seq[String]]) extends ApiHttpData {
    val contentType = "application/x-www-form-urlencoded"
  }
}
