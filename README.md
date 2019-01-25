
[![Build Status](https://travis-ci.org/scommons/scommons-api.svg?branch=master)](https://travis-ci.org/scommons/scommons-api)
[![Coverage Status](https://coveralls.io/repos/github/scommons/scommons-api/badge.svg?branch=master)](https://coveralls.io/github/scommons/scommons-api?branch=master)
[![Scala.js](https://www.scala-js.org/assets/badges/scalajs-0.6.17.svg)](https://www.scala-js.org)

## scommons-api
Common REST API Scala/Scala.js components

### How to add it to your project

Current version is under active development, but you already can try it:
```scala
val scommonsApiVer = "0.1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  // shared
  "org.scommons.api" %%% "scommons-api-core" % scommonsApiVer,
  "org.scommons.api" %%% "scommons-api-joda-time" % scommonsApiVer,

  // server/jvm only
  "org.scommons.api" %% "scommons-api-play-ws" % scommonsApiVer
)
```

Latest `SNAPSHOT` version is published to [Sonatype Repo](https://oss.sonatype.org/content/repositories/snapshots/org/scommons/), just make sure you added
the proper dependency resolver to your `build.sbt` settings:
```scala
resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
```

### How to Build

To build and run all the tests use the following command:
```bash
sbt clean test
```

## Documentation

You can find documentation [here](https://scommons.org/scommons-api)
