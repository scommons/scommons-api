package org.joda.time

/** Wrapper around ISO8601 time formatted string.
  *
  * @param isoString date and time in ISO8601 format `yyyy-MM-ddTHH:mm:ss.SSSZZ`
  */
case class DateTime(private val isoString: String) {

  require(isoString match {
    case DateTime.isoRegex(_*) => true
    case _ => false
  }, s"datetime string '$isoString' is not in ISO8601 format")

  /**
    * @return ISO8601 time formatted string, not `null`
    */
  override def toString: String = isoString
}

object DateTime {

  private val isoRegex = """(\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}.\d{3}([+-](\d{4}|\d{2}:\d{2})|[zZ]))""".r
}
