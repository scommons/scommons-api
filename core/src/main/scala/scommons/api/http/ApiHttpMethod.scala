package scommons.api.http

sealed trait ApiHttpMethod

object ApiHttpMethod {

  case object GET extends ApiHttpMethod
  case object POST extends ApiHttpMethod
  case object PUT extends ApiHttpMethod
  case object DELETE extends ApiHttpMethod
}
