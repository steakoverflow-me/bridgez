import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport.scalaJSUseMainModuleInitializer
import sbt.addCommandAlias
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport.startWebpackDevServer

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

    Test / requireJsDomEnv := true,
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule)), // configure Scala.js to emit a JavaScript module instead of a top-level script

    // hot reloading configuration:
    // https://github.com/scalacenter/scalajs-bundler/issues/180
    addCommandAlias("dev", "; compile; fastOptJS::startWebpackDevServer; devwatch; fastOptJS::stopWebpackDevServer"),
    addCommandAlias("devwatch", "~; fastOptJS; copyFastOptJS"),

    webpack / version := "4.46.0",
    startWebpackDevServer / version := "3.11.2",
    webpackDevServerExtraArgs := Seq("--color"),
    webpackDevServerPort := 8080,
    fastOptJS / webpackConfigFile := Some(baseDirectory.value / "webpack.config.dev.js"),
    fastOptJS / webpackBundlingMode := BundlingMode.LibraryOnly(), // https://scalacenter.github.io/scalajs-bundler/cookbook.html#performance

    // when running the "dev" alias, after every fastOptJS compile all artifacts are copied into
    // a folder which is served and watched by the webpack devserver.
    // this is a workaround for: https://github.com/scalacenter/scalajs-bundler/issues/180
    TaskKey[Unit]("copyFastOptJS", "Copy javascript files to target directory") := {
      val inDir = (Compile / fastOptJS / crossTarget).value
      val outDir = (Compile / fastOptJS / crossTarget).value / "dev"
      val files = Seq(name.value.toLowerCase + "-fastopt-loader.js", name.value.toLowerCase + "-fastopt.js") map { p => (inDir / p, outDir / p) }
      IO.copy(files, overwrite = true, preserveLastModified = true, preserveExecutable = true)
    }
).dependsOn(common)

lazy val bridgez = (project in file("."))
  .settings(
    name := "bridgez"
  ).dependsOn(frontend, backend)