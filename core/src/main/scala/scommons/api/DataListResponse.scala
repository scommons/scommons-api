package scommons.api

/** Common API data list response definition.
  *
  * @tparam T response list data type
  */
trait DataListResponse[T] extends ApiResponse {

  def dataList: Option[List[T]]
}
