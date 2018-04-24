package scommons.api

/** Common API page data response definition.
  *
  * @tparam T response list data type
  */
trait PageDataResponse[T] extends DataListResponse[T] {

  def totalCount: Option[Int]
}
