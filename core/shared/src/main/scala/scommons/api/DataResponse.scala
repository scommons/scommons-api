package scommons.api

/** Common API data response definition.
  *
  * @tparam T response data type
  */
trait DataResponse[T] extends ApiResponse {

  def data: Option[T]
}
