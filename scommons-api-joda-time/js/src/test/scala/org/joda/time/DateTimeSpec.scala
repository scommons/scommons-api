package org.joda.time

import org.scalatest.{FlatSpec, Matchers}

import scala.scalajs.js

class DateTimeSpec extends FlatSpec with Matchers {

  it should "fail if DateTime value is not ISO8601 time formatted string" in {
    //given
    val isoString = "2018-03-03T13:43:01.234+01"

    //when
    val e = the[IllegalArgumentException] thrownBy {
      DateTime(isoString)
    }

    //then
    e.getMessage should include (
      s"datetime string '$isoString' is not in ISO8601 format"
    )
  }

  it should "create DateTime with valid iso string" in {
    def dateTime(isoString: String): Unit = {
      DateTime(isoString).toString shouldBe isoString

      //check that it can also be parsed by javascript date
      new js.Date(isoString)
    }

    //when & then
    dateTime("2018-03-03T13:43:01.234z")
    dateTime("2018-03-03T13:43:01.234Z")
    dateTime("2018-03-03T13:43:01.234+01:00")
    dateTime("2018-03-03T13:43:01.234+00:30")
    dateTime("2018-03-03T13:43:01.234-01:00")
    dateTime("2018-03-03T13:43:01.234-00:30")
    dateTime("2018-03-03T13:43:01.234+0100")
    dateTime("2018-03-03T13:43:01.234+0030")
    dateTime("2018-03-03T13:43:01.234-0100")
    dateTime("2018-03-03T13:43:01.234-0030")
  }

  it should "perform value equality" in {
    //given
    val d1 = DateTime("2018-03-03T13:43:01.234Z")
    val d2 = DateTime("2018-03-03T13:43:01.234Z")

    //when & then
    d1 shouldBe d2
    (d1 == d2) shouldBe true
    d1 should not (be theSameInstanceAs d2)
  }
}
