package scommons.api.admin.permission

abstract class PermissionNode(val name: String,
                              val title: String,
                              nodes: List[PermissionNode] = Nil) {

  require(name.trim.nonEmpty, "name should be non empty")
  require(title.trim.nonEmpty, "title should be non empty")

  private var permissions: List[Permission] = Nil
  
  override def toString: String = name
  
  def getNodes: List[PermissionNode] = nodes
  
  def getPermissions: List[Permission] = permissions

  protected def add(p: Permission): Permission = {
    permissions = p +: permissions
    p
  }
}
