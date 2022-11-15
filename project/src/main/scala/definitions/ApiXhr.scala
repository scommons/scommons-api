package definitions

import common.{Libs, TestLibs}
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
      .settings(ScalaJsModule.settings: _*)
      .settings(
        description := "Scala Commons ApiHttpClient implementation using JavaScript XMLHttpRequest",

        // disable scoverage, until the following issue is fixed:
        //   https://github.com/scoverage/scalac-scoverage-plugin/issues/196
        coverageEnabled := false,
        coverageExcludedPackages := "scommons.api.http.xhr.raw"
      )
  }

  override val internalDependencies: Seq[ClasspathDep[ProjectReference]] = Seq(
    ApiCore.js
  )

  override val runtimeDependencies: Def.Initialize[Seq[ModuleID]] = Def.setting(Nil)

  override val testDependencies: Def.Initialize[Seq[ModuleID]] = Def.setting(Seq(
    TestLibs.scalaTestJs.value,
    TestLibs.scalaMockJs.value,
    Libs.scalaJsJavaSecureRandom.value
  ).map(_ % "test"))
}
