import definitions._
import scommons.sbtplugin.project.CommonModule
import scommons.sbtplugin.project.CommonModule.ideExcludedDirectories

lazy val `scommons-api` = (project in file("."))
  .settings(CommonModule.settings: _*)
  .settings(ApiModule.settings: _*)
  .settings(
    publish / skip := true,
    publish := ((): Unit),
    publishLocal := ((): Unit),
    publishM2 := ((): Unit)
  )
  .settings(
    ideExcludedDirectories += baseDirectory.value / "docs" / "_site"
  )
  .aggregate(
  `scommons-api-core-jvm`,
  `scommons-api-core-js`,
  `scommons-api-admin-jvm`,
  `scommons-api-admin-js`,
  `scommons-api-joda-time-jvm`,
  `scommons-api-joda-time-js`,
  `scommons-api-play-ws`,
  `scommons-api-xhr`
)

lazy val `scommons-api-core-jvm` = ApiCore.jvm
lazy val `scommons-api-core-js` = ApiCore.js
lazy val `scommons-api-admin-jvm` = ApiAdmin.jvm
lazy val `scommons-api-admin-js` = ApiAdmin.js
lazy val `scommons-api-joda-time-jvm` = ApiJodaTime.jvm
lazy val `scommons-api-joda-time-js` = ApiJodaTime.js
lazy val `scommons-api-play-ws` = ApiPlayWs.definition
lazy val `scommons-api-xhr` = ApiXhr.definition
