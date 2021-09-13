ThisBuild / scalaVersion     := "3.0.2"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "me.stackoverflow.bridgez"
ThisBuild / organizationName := "example"

val zioVersion = "2.0.0-M2"
val neotypesVersion = "0.18.3"
//val outwatchVersion = "97bc6df"
val outwatchVersion = "e2266b7"

lazy val common = (project in file("./common"))
  .settings(
    name := "backend",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio"      % zioVersion,
      "dev.zio" %% "zio-test" % zioVersion % Test
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )

lazy val backend = (project in file("./backend"))
  .settings(
    name := "backend",
    libraryDependencies ++= Seq(
      "io.github.neotypes" %% "neotypes-core"       % neotypesVersion,
      "io.github.neotypes" %% "neotypes-zio"        % neotypesVersion,
      "io.github.neotypes" %% "neotypes-zio-stream" % neotypesVersion
    ).map(dep => dep.cross(CrossVersion.for3Use2_13)),
  ).dependsOn(common)

lazy val frontend = (project in file("./frontend"))
  .enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)
  .settings(
    name := "frontend",
    resolvers += "jitpack" at "https://jitpack.io",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio-interop-cats" % "3.1.1.0"
    ),
    libraryDependencies ++= Seq(
      "com.github.outwatch.outwatch" %%% "outwatch"      % outwatchVersion,
      "com.github.outwatch.outwatch" %%% "outwatch-util" % outwatchVersion, // Store, Websocket, Http
    ).map(dep => dep.cross(CrossVersion.for3Use2_13)),
  ).dependsOn(common)

lazy val bridgez = (project in file("."))
  .settings(
    name := "bridgez"
  ).dependsOn(frontend, backend)