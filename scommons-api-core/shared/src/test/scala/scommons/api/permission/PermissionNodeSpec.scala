package scommons.api.permission

import org.scalatest.{FlatSpec, Matchers}
import scommons.api.permission.PermissionNodeSpec._

class PermissionNodeSpec extends FlatSpec with Matchers {

  it should "fail to create new PermissionNode if name is empty" in {
    //when
    val e = the[IllegalArgumentException] thrownBy {
      new PermissionNode("", "Test") {}
    }
    
    //then
    e.getMessage should include("name should be non empty")
  }
  
  it should "fail to create new PermissionNode if title is empty" in {
    //when
    val e = the[IllegalArgumentException] thrownBy {
      new PermissionNode("test", "") {}
    }
    
    //then
    e.getMessage should include("title should be non empty")
  }
  
  it should "return name when toString" in {
    //given
    val node = TestPermNode
    
    //when
    val result = node.toString
    
    //then
    result shouldBe TestPermNode.name
  }
  
  it should "return list of child nodes" in {
    //given
    val node = TestPermNode
    
    //when
    val result = node.getNodes
    
    //then
    result shouldBe List(TestPermSubNode)
  }
  
  it should "return list of permissions" in {
    //given
    val node = TestPermNode
    
    //when
    val result = node.getPermissions
    
    //then
    result shouldBe List(TestPermNode.testWrite, TestPermNode.testRead)
  }
}

object PermissionNodeSpec {
  
  object TestPermNode extends PermissionNode("testPermNode", "TestPermNode", List(TestPermSubNode)) {
    
    val testRead = add(Permission("testRead", "TestRead"))
    val testWrite = add(Permission("testWrite", "TestWrite"))
  }
  
  object TestPermSubNode extends PermissionNode("testPermSubNode", "TestPermSubNode") {
    
    val testDelete = add(Permission("testDelete", "TestDelete"))
  }
}
