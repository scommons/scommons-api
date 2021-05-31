package scommons.api

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class StatusResponseSpec extends AnyFlatSpec with Matchers {

  it should "return successful status" in {
    //when & then
    StatusResponse.Ok.status shouldBe ApiStatus.Ok
  }
}
