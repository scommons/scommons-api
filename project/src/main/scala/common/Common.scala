package common

import org.sbtidea.SbtIdeaPlugin.ideaExcludeFolders
import sbt.Keys._
import sbt._
import scoverage.ScoverageKeys._

object Common {

  val settings: Seq[Setting[_]] = Seq(
    organization := "org.scommons.api",
    scalaVersion := "2.12.2",
    scalacOptions ++= Seq(
      //"-Xcheckinit",
      "-Xfatal-warnings",
      "-Xlint:_",
      "-explaintypes",
      "-unchecked",
      "-deprecation",
      "-feature"
    ),
    //ivyScala := ivyScala.value map {
    //  _.copy(overrideScalaVersion = true)
    //},
    ideaExcludeFolders := {
      val base = baseDirectory.value
      List(
        s"$base/.idea",
        s"$base/target"
      )
    },
    //when run tests with coverage: "sbt clean coverage test it:test coverageReport && sbt coverageAggregate"
    coverageMinimum := 80,

    resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
    //
    // publish/release related settings:
    //
    publishMavenStyle := true,
    publishArtifact in Test := false,
    publishTo := {
      if (isSnapshot.value)
        Some("snapshots" at "https://oss.sonatype.org/content/repositories/snapshots")
      else
        Some("releases" at "https://oss.sonatype.org/service/local/staging/deploy/maven2")
    },
    pomExtra := {
      <url>https://github.com/scommons/scommons-api</url>
        <licenses>
          <license>
            <name>Apache 2</name>
            <url>http://www.apache.org/licenses/LICENSE-2.0</url>
            <distribution>repo</distribution>
          </license>
        </licenses>
        <scm>
          <url>git@github.com:scommons/scommons-api.git</url>
          <connection>scm:git@github.com:scommons/scommons-api.git</connection>
        </scm>
        <developers>
          <developer>
            <id>viktorp</id>
            <name>Viktor Podzigun</name>
            <url>https://github.com/viktor-podzigun</url>
          </developer>
        </developers>
    },
    pomIncludeRepository := {
      _ => false
    }
  )
}
