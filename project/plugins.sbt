//resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"
resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.1.0")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.20")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.6.0")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")
addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.2.4")
