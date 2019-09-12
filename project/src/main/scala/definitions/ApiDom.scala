package definitions

import common.{Libs, TestLibs}
import org.scalajs.sbtplugin.ScalaJSPlugin
import sbt.Keys._
import sbt._
import scoverage.ScoverageKeys._

object ApiDom extends ApiModule {

  override val id: String = "scommons-api-dom"

  override val base: File = file("dom")

  override def definition: Project = {
    super.definition
      .enablePlugins(ScalaJSPlugin)
      .settings(
        description := "Common Scala ApiHttpClient implementation using dom XMLHttpRequest",

        // disable scoverage, until the following issue is fixed:
        //   https://github.com/scoverage/scalac-scoverage-plugin/issues/196
        coverageEnabled := false,

        //Opt-in @ScalaJSDefined by default
        scalacOptions += "-P:scalajs:sjsDefinedByDefault"
      )
  }

  override val internalDependencies: Seq[ClasspathDep[ProjectReference]] = Seq(
    ApiCore.js
  )

  override val runtimeDependencies: Def.Initialize[Seq[ModuleID]] = Def.setting(Seq(
    Libs.scalajsDom.value
  ))

  override val testDependencies: Def.Initialize[Seq[ModuleID]] = Def.setting(Seq(
    TestLibs.scalaTestJs.value,
    TestLibs.scalaMockJs.value
  ).map(_ % "test"))
}
