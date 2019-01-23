package scommons.api.jodatime

import org.joda.time.DateTime
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
}
