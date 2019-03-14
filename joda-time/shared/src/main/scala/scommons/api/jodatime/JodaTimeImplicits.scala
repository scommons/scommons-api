package scommons.api.jodatime

import org.joda.time.{DateTime, LocalDate, LocalTime}
import play.api.libs.json._

import scala.util.Try

object JodaTimeImplicits {

  implicit val dateTimeReads: Reads[DateTime] = new Reads[DateTime] {
    def reads(json: JsValue): JsResult[DateTime] = json match {
      case JsString(u) =>
        Try {
          new DateTime(u)
        }
          .map(JsSuccess(_))
          .getOrElse(JsError("error.expected.datetime.isoString"))
      case _ =>
        JsError("error.expected.jsstring")
    }
  }
  implicit val dateTimeWrites: Writes[DateTime] = new Writes[DateTime] {
    override def writes(o: DateTime): JsValue = JsString(o.toString)
  }
  
  implicit val dateReads: Reads[LocalDate] = new Reads[LocalDate] {
    def reads(json: JsValue): JsResult[LocalDate] = json match {
      case JsString(u) =>
        Try {
          new LocalDate(u)
        }
          .map(JsSuccess(_))
          .getOrElse(JsError("error.expected.date.isoString"))
      case _ =>
        JsError("error.expected.jsstring")
    }
  }
  implicit val dateWrites: Writes[LocalDate] = new Writes[LocalDate] {
    override def writes(o: LocalDate): JsValue = JsString(o.toString)
  }
  
  implicit val timeReads: Reads[LocalTime] = new Reads[LocalTime] {
    def reads(json: JsValue): JsResult[LocalTime] = json match {
      case JsString(u) =>
        Try {
          new LocalTime(u)
        }
          .map(JsSuccess(_))
          .getOrElse(JsError("error.expected.time.isoString"))
      case _ =>
        JsError("error.expected.jsstring")
    }
  }
  implicit val timeWrites: Writes[LocalTime] = new Writes[LocalTime] {
    override def writes(o: LocalTime): JsValue = JsString(o.toString)
  }
}
