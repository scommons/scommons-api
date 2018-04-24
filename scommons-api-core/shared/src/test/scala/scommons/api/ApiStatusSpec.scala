package scommons.api

import org.scalatest.{FlatSpec, Matchers}

class ApiStatusSpec extends FlatSpec with Matchers {

  it should "return successful status" in {
    //when
    val result = ApiStatus.Ok

    //then
    result.successful shouldBe true
    result.nonSuccessful shouldBe false
    result.code shouldBe 0
    result.error shouldBe None
    result.details shouldBe None
  }

  it should "return nonSuccessful status" in {
    //when
    val result = ApiStatus(400, "Bad Request")

    //then
    result.successful shouldBe false
    result.nonSuccessful shouldBe true
    result.code shouldBe 400
    result.error shouldBe Some("Bad Request")
    result.details shouldBe None
  }

  it should "return nonSuccessful status with details" in {
    //when
    val result = ApiStatus(400, "Bad Request", "some details")

    //then
    result.successful shouldBe false
    result.nonSuccessful shouldBe true
    result.code shouldBe 400
    result.error shouldBe Some("Bad Request")
    result.details shouldBe Some("some details")
  }
}
