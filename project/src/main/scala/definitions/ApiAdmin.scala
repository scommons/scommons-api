package definitions

import common.{Libs, TestLibs}
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import org.scalajs.sbtplugin.cross.CrossProject
import sbt.Keys._
import sbt._
import scommons.sbtplugin.project.CommonModule
import scoverage.ScoverageKeys._

object ApiAdmin {

  val id: String = "scommons-api-admin"

  def base: File = file(id)

  lazy val `scommons-api-admin`: CrossProject = crossProject.in(base)
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
