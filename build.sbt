import common.Common
import definitions._

lazy val `scommons-api` = (project in file("."))
  .settings(Common.settings)
  .settings(
    skip in publish := true,
    publish := (),
    publishM2 := ()
  )
  .settings(
    ideaExcludeFolders += s"${baseDirectory.value}/docs/_site"
  )
  .aggregate(
  `scommons-api-core-jvm`,
  `scommons-api-core-js`,
  `scommons-api-joda-time-jvm`,
  `scommons-api-joda-time-js`,
  `scommons-api-play-ws`
)

lazy val `scommons-api-core-jvm` = ScommonsApiCore.jvm
lazy val `scommons-api-core-js` = ScommonsApiCore.js
lazy val `scommons-api-joda-time-jvm` = ScommonsApiJodaTime.jvm
lazy val `scommons-api-joda-time-js` = ScommonsApiJodaTime.js
lazy val `scommons-api-play-ws` = ScommonsApiPlayWs.definition
