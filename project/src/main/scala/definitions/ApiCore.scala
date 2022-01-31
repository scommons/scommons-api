package definitions

import common.{Libs, TestLibs}
import sbt.Keys._
import sbt._
import sbtcrossproject.CrossPlugin.autoImport._
import sbtcrossproject.{CrossProject, JVMPlatform}
import scalajscrossproject.ScalaJSCrossPlugin.autoImport._
import scommons.sbtplugin.project.CommonModule

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
      libraryDependencies ++= Seq(
        Libs.jodaTime.value
      )
    ).jsSettings(
      ScalaJsModule.settings: _*
    )

  lazy val jvm: Project = `scommons-api-core`.jvm
  lazy val js: Project = `scommons-api-core`.js
}
