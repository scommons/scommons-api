package scommons.api.admin.permission

import org.scalatest.{FlatSpec, Matchers}

class PermissionSpec extends FlatSpec with Matchers {

  it should "fail to create new Permission if name is empty" in {
    //when
    val e = the[IllegalArgumentException] thrownBy {
      Permission("", "Test")
    }
    
    //then
    e.getMessage should include("name should not be empty")
  }
  
  it should "fail to create new Permission if title is empty" in {
    //when
    val e = the[IllegalArgumentException] thrownBy {
      Permission("test", "")
    }
    
    //then
    e.getMessage should include("title should not be empty")
  }
  
  it should "return name when toString" in {
    //when & then
    Permission("testName", "TestTitle").toString shouldBe "testName"
  }
  
  it should "define reusable permissions" in {
    def assertPermission(perm: Permission, expected: (String, String)): Unit = {
      perm match {
        case p => (p.name, p.title) shouldBe expected
      }
    }
    
    //when & then
    assertPermission(Permission.read, ("read", "Read"))
    assertPermission(Permission.create, ("create", "Create"))
    assertPermission(Permission.update, ("update", "Update"))
    assertPermission(Permission.rename, ("rename", "Rename"))
    assertPermission(Permission.delete, ("delete", "Delete"))
    assertPermission(Permission.print, ("print", "Print"))
  }
}
