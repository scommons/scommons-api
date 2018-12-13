package scommons.api.permission

case class Permission(name: String, title: String) {
  
  require(name.trim.nonEmpty, "name should be non empty")
  require(title.trim.nonEmpty, "title should be non empty")

  override def toString: String = name
}

object Permission {

  val read = Permission("read", "Read")
  val create = Permission("create", "Create")
  val update = Permission("update", "Update")
  val rename = Permission("rename", "Rename")
  val delete = Permission("delete", "Delete")
  val print = Permission("print", "Print")
}
