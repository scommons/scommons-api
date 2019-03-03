package definitions

import common.TestLibs
import sbt.Keys._
import sbt._
import sbtcrossproject.CrossPlugin.autoImport._
import sbtcrossproject.{CrossProject, JVMPlatform}
import scalajscrossproject.ScalaJSCrossPlugin.autoImport._
import scommons.sbtplugin.project.CommonModule

object ApiAdmin {

  val id: String = "scommons-api-admin"

  val base: File = file("admin")

  lazy val `scommons-api-admin`: CrossProject = CrossProject(id, base)(JSPlatform, JVMPlatform)
    .settings(CommonModule.settings: _*)
    .settings(ApiModule.settings: _*)
    .dependsOn(ApiCore.`scommons-api-core`)
    .settings(
      description := "Common Scala/Scala.js Admin REST API protocol definitions",

      libraryDependencies ++= Seq(
        TestLibs.scalaTestJs.value % "test",
        TestLibs.scalaMockJs.value % "test"
      )
    ).jvmSettings(
      // Add JVM-specific settings here
    ).jsSettings(
      // Add JS-specific settings here
    )

  lazy val jvm: Project = `scommons-api-admin`.jvm
  lazy val js: Project = `scommons-api-admin`.js
}
