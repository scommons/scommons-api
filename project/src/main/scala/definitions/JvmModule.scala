package definitions

import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt.Keys._
import sbt._

object JvmModule {

  val settings: Seq[Setting[_]] = Seq(
    // avoid double-publishing when cross-compiling to Scala.js 1.1+
    skip in publish := !scalaJSVersion.startsWith("0.6")
  )
}
