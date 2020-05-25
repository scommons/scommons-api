package definitions

import common.TestLibs
import org.scalajs.sbtplugin.ScalaJSPlugin
import sbt.Keys._
import sbt._
import scoverage.ScoverageKeys._

object ApiXhr extends ApiModule {

  override val id: String = "scommons-api-xhr"

  override val base: File = file("xhr")

  override def definition: Project = {
    super.definition
      .enablePlugins(ScalaJSPlugin)
      .settings(
        description := "Scala Commons ApiHttpClient implementation using JavaScript XMLHttpRequest",

        // disable scoverage, until the following issue is fixed:
        //   https://github.com/scoverage/scalac-scoverage-plugin/issues/196
        coverageEnabled := false,
        coverageExcludedPackages := "scommons.api.http.xhr.raw",

        //Opt-in @ScalaJSDefined by default
        scalacOptions += "-P:scalajs:sjsDefinedByDefault"
      )
  }

  override val internalDependencies: Seq[ClasspathDep[ProjectReference]] = Seq(
    ApiCore.js
  )

  override val runtimeDependencies: Def.Initialize[Seq[ModuleID]] = Def.setting(Nil)

  override val testDependencies: Def.Initialize[Seq[ModuleID]] = Def.setting(Seq(
    TestLibs.scalaTestJs.value,
    TestLibs.scalaMockJs.value
  ).map(_ % "test"))
}
