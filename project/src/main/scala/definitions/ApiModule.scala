package definitions

import sbt.Keys._
import sbt._
import scommons.sbtplugin.project.CommonModule

trait ApiModule extends CommonModule {

  override val repoName = "scommons-api"

  override def definition: Project = {
    super.definition
      .settings(ApiModule.settings: _*)
  }
}

object ApiModule {

  val settings: Seq[Setting[_]] = Seq(
    organization := "org.scommons.api",
    
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
