
[![Build Status](https://travis-ci.com/scommons/scommons-api.svg?branch=master)](https://travis-ci.com/scommons/scommons-api)
[![Coverage Status](https://coveralls.io/repos/github/scommons/scommons-api/badge.svg?branch=master)](https://coveralls.io/github/scommons/scommons-api?branch=master)
[![scala-index](https://index.scala-lang.org/scommons/scommons-api/scommons-api-core/latest.svg)](https://index.scala-lang.org/scommons/scommons-api/scommons-api-core)
[![Scala.js](https://www.scala-js.org/assets/badges/scalajs-0.6.29.svg)](https://www.scala-js.org)

## scommons-api
Common REST API Scala/Scala.js components

### How to add it to your project

```scala
val scommonsApiVer = "1.0.0-SNAPSHOT"

libraryDependencies ++= Seq(
  // shared
  "org.scommons.api" %%% "scommons-api-core" % scommonsApiVer,
  "org.scommons.api" %%% "scommons-api-joda-time" % scommonsApiVer,

  // client/js only
  "org.scommons.api" %%% "scommons-api-xhr" % scommonsApiVer,
  
  // server/jvm only
  "org.scommons.api" %% "scommons-api-play-ws" % scommonsApiVer
)
```

Latest `SNAPSHOT` version is published to [Sonatype Repo](https://oss.sonatype.org/content/repositories/snapshots/org/scommons/), just make sure you added
the proper dependency resolver to your `build.sbt` settings:
```scala
resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
```

### How to use it

#### Joda Date/Time

You can use in your shared API data case classes date/time types from
the [joda](https://www.joda.org/joda-time/) library:
```scala
import org.joda.time._
import play.api.libs.json._

case class User(firstName: String,
                lastName: String,
                registeredAt: DateTime,
                birthDate: LocalDate,
                birthTime: LocalTime)

object User {
  // you only need to provide appropriate implicits:
  import scommons.api.jodatime.JodaTimeImplicits.{dateTimeReads => dtReads, dateTimeWrites => dtWrites}
  import scommons.api.jodatime.JodaTimeImplicits.{dateReads => dReads, dateWrites => dWrites}
  import scommons.api.jodatime.JodaTimeImplicits.{timeReads => tReads, timeWrites => tWrites}

  implicit val jsonFormat: Format[User] = Json.format[User]
}
```

For `JS` part the following types are defined as wrappers around
the corresponding `ISO time formatted string`
and do not contain any logic:
- [DateTime](joda-time/js/src/main/scala/org/joda/time/DateTime.scala) => [tests](joda-time/js/src/test/scala/org/joda/time/DateTimeSpec.scala)
- [LocalDate](joda-time/js/src/main/scala/org/joda/time/LocalDate.scala) => [tests](joda-time/js/src/test/scala/org/joda/time/LocalDateSpec.scala)
- [LocalTime](joda-time/js/src/main/scala/org/joda/time/LocalTime.scala) => [tests](joda-time/js/src/test/scala/org/joda/time/LocalTimeSpec.scala)

Once you receive from an API on `JS` side an object that uses them
you can use `toString` method to get an `ISO time formatted string`
and pass it to your favorite `JS time library` to parse it.

For example, you could use standard [JS Date](https://www.w3schools.com/jS/js_date_methods.asp)
class to parse it:
```scala
import scala.scalajs.js

new js.Date(user.registeredAt.toString).getDate
```

### How to Build

To build and run all the tests use the following command:
```bash
sbt test
```

## Documentation

You can find more documentation [here](https://scommons.org/scommons-api)
