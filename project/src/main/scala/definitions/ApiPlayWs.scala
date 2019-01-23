package definitions

import common.{Libs, TestLibs}
import sbt.Keys._
import sbt._

object ApiPlayWs extends ApiModule {

  override val id: String = "scommons-api-play-ws"

  override val base: File = file("play-ws")

  override def definition: Project = super.definition
    .settings(
      description := "Common Scala ApiHttpClient implementation using play-ws library"
    )

  override val internalDependencies: Seq[ClasspathDep[ProjectReference]] = Seq(
    ApiCore.jvm
  )

  override val runtimeDependencies: Def.Initialize[Seq[ModuleID]] = Def.setting(Seq(
    Libs.playWs.value
  ))

  override val testDependencies: Def.Initialize[Seq[ModuleID]] = Def.setting(Seq(
    TestLibs.scalaTest.value,
    TestLibs.mockito.value
  ).map(_ % "test"))
}
