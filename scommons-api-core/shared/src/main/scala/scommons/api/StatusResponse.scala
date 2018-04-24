package scommons.api

import play.api.libs.json._

case class StatusResponse(status: ApiStatus) extends ApiResponse

object StatusResponse {

  val Ok = StatusResponse(ApiStatus.Ok)

  implicit val jsonFormat: Format[StatusResponse] = Json.format[StatusResponse]
}
