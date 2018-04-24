package definitions

import sbt._

trait ProjectDef {

  val id: String

  def base: File = file(id)

  def definition: Project

  val runtimeDependencies: Def.Initialize[Seq[ModuleID]]

  val testDependencies: Def.Initialize[Seq[ModuleID]]

  val internalDependencies: Seq[ClasspathDep[ProjectReference]]

}