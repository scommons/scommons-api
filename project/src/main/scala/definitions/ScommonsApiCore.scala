package definitions

import common.{Common, Libs, TestLibs}
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import org.scalajs.sbtplugin.cross.CrossProject
import sbt.Keys._
import sbt._
import scoverage.ScoverageKeys._

object ScommonsApiCore {

  val id: String = "scommons-api-core"

  def base: File = file(id)

  lazy val `scommons-api-core`: CrossProject = crossProject.in(base)
    .settings(Common.settings: _*)
    .settings(
      description := "Common Scala/Scala.js REST API protocol definitions",

      libraryDependencies ++= Seq(
        Libs.playJsonJs.value,
        TestLibs.scalaTestJs.value % "test",
        TestLibs.scalaMockJs.value % "test"
      )
    ).jvmSettings(
      // Add JVM-specific settings here
    ).jsSettings(
      // disable scoverage, until the following issue is fixed:
      //   https://github.com/scoverage/scalac-scoverage-plugin/issues/196
      coverageEnabled := false,

      //Opt-in @ScalaJSDefined by default
      scalacOptions += "-P:scalajs:sjsDefinedByDefault",

      libraryDependencies ++= Seq(
        Libs.scalajsDom.value
      )
    )

  lazy val jvm: Project = `scommons-api-core`.jvm

  lazy val js: Project = `scommons-api-core`.js
}