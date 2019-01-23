package scommons.api.admin.permission

class Permission private(val name: String, val title: String) {
  
  require(name.trim.nonEmpty, "name should not be empty")
  require(title.trim.nonEmpty, "title should not be empty")

  override def toString: String = name
}

object Permission {

  def apply(name: String, title: String): Permission =
    new Permission(name.trim, title.trim)

  def read = Permission("read", "Read")
  def create = Permission("create", "Create")
  def update = Permission("update", "Update")
  def rename = Permission("rename", "Rename")
  def delete = Permission("delete", "Delete")
  def print = Permission("print", "Print")
}
