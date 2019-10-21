package scommons.api.http

case class ApiHttpResponse(url: String,
                           status: Int,
                           headers: Map[String, Seq[String]],
                           body: String)
