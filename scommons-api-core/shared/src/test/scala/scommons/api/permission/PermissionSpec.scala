package scommons.api.permission

import org.scalatest.{FlatSpec, Matchers}

class PermissionSpec extends FlatSpec with Matchers {

  it should "fail to create new Permission if name is empty" in {
    //when
    val e = the[IllegalArgumentException] thrownBy {
      Permission("", "Test")
    }
    
    //then
    e.getMessage should include("name should be non empty")
  }
  
  it should "fail to create new Permission if title is empty" in {
    //when
    val e = the[IllegalArgumentException] thrownBy {
      Permission("test", "")
    }
    
    //then
    e.getMessage should include("title should be non empty")
  }
  
  it should "return name when toString" in {
    //when & then
    Permission("testName", "TestTitle").toString shouldBe "testName"
  }
  
  it should "define reusable permissions" in {
    //when & then
    Permission.read shouldBe Permission("read", "Read")
    Permission.create shouldBe Permission("create", "Create")
    Permission.update shouldBe Permission("update", "Update")
    Permission.rename shouldBe Permission("rename", "Rename")
    Permission.delete shouldBe Permission("delete", "Delete")
    Permission.print shouldBe Permission("print", "Print")
  }
}
