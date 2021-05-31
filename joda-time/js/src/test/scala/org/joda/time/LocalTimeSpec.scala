package org.joda.time

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.scalajs.js

class LocalTimeSpec extends AnyFlatSpec with Matchers {

  it should "fail if LocalTime value is not ISO8601 time formatted string" in {
    //given
    val isoString = "13:43:01.234Z"

    //when
    val e = the[IllegalArgumentException] thrownBy {
      LocalTime(isoString)
    }

    //then
    e.getMessage should include (
      s"time string '$isoString' is not in ISO8601 format (HH:mm:ss.SSS)"
    )
  }

  it should "create LocalTime with valid iso string" in {
    def time(isoTime: String): Unit = {
      LocalTime(isoTime).toString shouldBe isoTime

      //check that it can also be parsed by javascript date
      new js.Date(s"2019-03-12T${isoTime}Z").toISOString() shouldBe s"2019-03-12T${isoTime}Z"
    }

    //when & then
    time("13:43:01.234")
  }

  it should "perform value equality" in {
    //given
    val d1 = LocalTime("13:43:01.234")
    val d2 = LocalTime("13:43:01.234")

    //when & then
    d1 shouldBe d2
    (d1 == d2) shouldBe true
    d1 should not (be theSameInstanceAs d2)
  }
}
