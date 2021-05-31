package scommons.api.admin.permission

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scommons.api.admin.permission.PermissionNodeSpec._

class PermissionNodeSpec extends AnyFlatSpec with Matchers {

  it should "fail to create new PermissionNode if name is empty" in {
    //when
    val e = the[IllegalArgumentException] thrownBy {
      new PermissionNode("", "Test") {}
    }
    
    //then
    e.getMessage should include("name should not be empty")
  }
  
  it should "fail to create new PermissionNode if title is empty" in {
    //when
    val e = the[IllegalArgumentException] thrownBy {
      new PermissionNode("test", "") {}
    }
    
    //then
    e.getMessage should include("title should not be empty")
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
    val result = node.nodes
    
    //then
    result shouldBe List(TestPermSubNode)
  }
  
  it should "return list of permissions" in {
    //given
    val node = TestPermNode
    
    //when
    val result = node.permissions
    
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
