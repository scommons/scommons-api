package common

import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt._

object Libs {

  private val playVer = "2.6.7"
  private val playWsVer = "1.1.3"

  lazy val playJson = Def.setting("com.typesafe.play" %% "play-json" % playVer)
  lazy val playWs = Def.setting("com.typesafe.play" %% "play-ahc-ws-standalone" % playWsVer)

  // Scala.js dependencies

  lazy val playJsonJs = Def.setting("com.typesafe.play" %%% "play-json" % playVer)

  lazy val scalajsDom = Def.setting("org.scala-js" %%% "scalajs-dom" % "0.9.2")
}
