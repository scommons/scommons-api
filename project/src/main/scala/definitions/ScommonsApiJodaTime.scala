package definitions

import common.{Common, Libs, TestLibs}
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import org.scalajs.sbtplugin.cross.CrossProject
import sbt.Keys._
import sbt._

object ScommonsApiJodaTime {

  val id: String = "scommons-api-joda-time"

  def base: File = file(id)

  lazy val `scommons-api-joda-time`: CrossProject = crossProject.in(base)
    .settings(Common.settings: _*)
    .settings(
      description := "Common Scala/Scala.js joda-time play-json conversions for REST API",

      libraryDependencies ++= Seq(
        Libs.playJsonJs.value,
        TestLibs.scalaTestJs.value % "test",
        TestLibs.scalaMockJs.value % "test"
      )
    ).jvmSettings(
      // Add JVM-specific settings here
    ).jsSettings(
      // Add JS-specific settings here
    )

  lazy val jvm: Project = `scommons-api-joda-time`.jvm

  lazy val js: Project = `scommons-api-joda-time`.js
}
