package scommons.api

import play.api.libs.json._

/** Common response status definition.
  *
  * @param code status code, non-zero in case of error
  * @param error optional error message, describes error status code
  * @param details optional error details
  */
case class ApiStatus private(code: Int,
                             error: Option[String],
                             details: Option[String]) {

  def successful: Boolean = code == ApiStatus.Ok.code

  def nonSuccessful: Boolean = !successful
}

object ApiStatus {

  val Ok = ApiStatus(0, None, None) // successful operation

  implicit val jsonFormat: Format[ApiStatus] = Json.format[ApiStatus]

  def apply(code: Int, error: String): ApiStatus =
    ApiStatus(code, Some(error), None)

  def apply(code: Int, error: String, details: String): ApiStatus =
    ApiStatus(code, Some(error), Some(details))
}
