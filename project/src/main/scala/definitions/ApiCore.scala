package definitions

import common.{Libs, TestLibs}
import sbt.Keys._
import sbt._
import sbtcrossproject.CrossPlugin.autoImport._
import sbtcrossproject.{CrossProject, JVMPlatform}
import scommons.sbtplugin.project.CommonModule

import scalajscrossproject.ScalaJSCrossPlugin.autoImport._

object ApiCore {

  val id: String = "scommons-api-core"

  val base: File = file("core")

  lazy val `scommons-api-core`: CrossProject = CrossProject(id, base)(JSPlatform, JVMPlatform)
    .crossType(CrossType.Pure)
    .settings(CommonModule.settings: _*)
    .settings(ApiModule.settings: _*)
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
      //Opt-in @ScalaJSDefined by default
      scalacOptions += "-P:scalajs:sjsDefinedByDefault"
    )

  lazy val jvm: Project = `scommons-api-core`.jvm
  lazy val js: Project = `scommons-api-core`.js
}
