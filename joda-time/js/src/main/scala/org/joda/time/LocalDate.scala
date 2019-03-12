package org.joda.time

/** Wrapper around ISO8601 time formatted string.
  *
  * @param isoString date in ISO8601 format `yyyy-MM-dd`
  */
case class LocalDate(private val isoString: String) {

  require(isoString match {
    case LocalDate.isoRegex(_*) => true
    case _ => false
  }, s"date string '$isoString' is not in ISO8601 format (yyyy-MM-dd)")

  /**
    * @return ISO8601 date formatted string, not `null`
    */
  override def toString: String = isoString
}

object LocalDate {

  private val isoRegex = """(\d{4}-\d{2}-\d{2})""".r
}
