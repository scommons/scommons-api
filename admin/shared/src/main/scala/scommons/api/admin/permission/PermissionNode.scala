package scommons.api.admin.permission

abstract class PermissionNode(val name: String,
                              val title: String,
                              val nodes: List[PermissionNode] = Nil) {

  require(name.trim.nonEmpty, "name should not be empty")
  require(title.trim.nonEmpty, "title should not be empty")

  private var _permissions: List[Permission] = Nil
  
  def permissions: List[Permission] = _permissions

  protected def add(p: Permission): Permission = {
    _permissions = p +: _permissions
    p
  }
  
  override def toString: String = name
}
