package definitions

import common.Common
import sbt.Keys._
import sbt._

trait BasicModule extends ProjectDef {

  def definition: Project = Project(id = id, base = base)
    .dependsOn(internalDependencies: _*)
    .settings(Common.settings: _*)
    .settings(
      libraryDependencies ++= (runtimeDependencies.value ++ testDependencies.value)
    )
}
