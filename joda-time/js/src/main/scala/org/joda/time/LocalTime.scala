package org.joda.time

/** Wrapper around ISO8601 time formatted string.
  *
  * @param isoString time in ISO8601 format `HH:mm:ss.SSS`
  */
case class LocalTime(private val isoString: String) {

  require(isoString match {
    case LocalTime.isoRegex(_*) => true
    case _ => false
  }, s"time string '$isoString' is not in ISO8601 format (HH:mm:ss.SSS)")

  /**
    * @return ISO8601 time formatted string, not `null`
    */
  override def toString: String = isoString
}

object LocalTime {

  private val isoRegex = """(\d{2}:\d{2}:\d{2}.\d{3})""".r
}
