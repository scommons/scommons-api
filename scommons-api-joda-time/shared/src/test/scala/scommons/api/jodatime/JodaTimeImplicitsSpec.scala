package scommons.api.jodatime

import org.joda.time.DateTime
import org.scalatest.{FlatSpec, Matchers}
import play.api.libs.json._
import scommons.api.jodatime.JodaTimeImplicitsSpec._

class JodaTimeImplicitsSpec extends FlatSpec with Matchers {

  private val expectedData = TestData(
    firstName = "John",
    lastName = "Doe",
    registeredAt = new DateTime("2018-03-03T13:43:01.234Z")
  )

  private val expectedJson =
    s"""{
       |  "firstName" : "John",
       |  "lastName" : "Doe",
       |  "registeredAt" : "${formatDateTime(expectedData.registeredAt)}"
       |}""".stripMargin

  it should "serialize DateTime to json" in {
    //given
    val data: TestData = expectedData

    //when & then
    Json.prettyPrint(Json.toJson(data)) shouldBe expectedJson
  }

  it should "deserialize DateTime from json" in {
    //when & then
    Json.parse(expectedJson).as[TestData] shouldBe expectedData
  }

  it should "fail if datetime field is not string" in {
    //given
    val json = Json.parse(expectedJson).as[JsObject] ++ Json.obj(
      "registeredAt" -> 123
    )

    //when
    val e = the[JsResultException] thrownBy {
      json.as[TestData]
    }

    //then
    e.getMessage should include (
      "/registeredAt,List(JsonValidationError(List(error.expected.jsstring)"
    )
  }

  it should "fail if datetime field is not ISO8601 time formatted string" in {
    //given
    val json = Json.parse(expectedJson).as[JsObject] ++ Json.obj(
      "registeredAt" -> "Mar, 3, 2018"
    )

    //when
    val e = the[JsResultException] thrownBy {
      json.as[TestData]
    }

    //then
    e.getMessage should include (
      "/registeredAt,List(JsonValidationError(List(error.expected.datetime.isoString)"
    )
  }

  private def formatDateTime(dt: DateTime): String = dt.toString
}

object JodaTimeImplicitsSpec {

  case class TestData(firstName: String,
                      lastName: String,
                      registeredAt: DateTime)

  object TestData {
    import scommons.api.jodatime.JodaTimeImplicits.{dateTimeReads => dtReads, dateTimeWrites => dtWrites}

    implicit val jsonFormat: Format[TestData] = Json.format[TestData]
  }
}
