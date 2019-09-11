package scommons.api.http

case class ApiHttpTimeoutException(url: String)
  extends RuntimeException(
    s"""Request timed out, unable to get timely response for:
       |  $url""".stripMargin
  )
