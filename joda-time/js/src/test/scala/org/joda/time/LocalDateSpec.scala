package org.joda.time

import org.scalatest.{FlatSpec, Matchers}

import scala.scalajs.js

class LocalDateSpec extends FlatSpec with Matchers {

  it should "fail if LocalDate value is not ISO8601 time formatted string" in {
    //given
    val isoString = "12.03.2019"

    //when
    val e = the[IllegalArgumentException] thrownBy {
      LocalDate(isoString)
    }

    //then
    e.getMessage should include (
      s"date string '$isoString' is not in ISO8601 format (yyyy-MM-dd)"
    )
  }

  it should "create LocalDate with valid iso string" in {
    def date(isoString: String): Unit = {
      LocalDate(isoString).toString shouldBe isoString

      //check that it can also be parsed by javascript date
      new js.Date(isoString)
    }

    //when & then
    date("2019-03-12")
  }

  it should "perform value equality" in {
    //given
    val d1 = LocalDate("2019-03-12")
    val d2 = LocalDate("2019-03-12")

    //when & then
    d1 shouldBe d2
    (d1 == d2) shouldBe true
    d1 should not (be theSameInstanceAs d2)
  }
}
