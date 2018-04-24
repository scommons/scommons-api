package scommons.api

import org.scalatest.{FlatSpec, Matchers}

class StatusResponseSpec extends FlatSpec with Matchers {

  it should "return successful status" in {
    //when & then
    StatusResponse.Ok.status shouldBe ApiStatus.Ok
  }
}
