ThisBuild / scalaVersion     := "3.0.2"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "me.stackoverflow.bridgez"
ThisBuild / organizationName := "example"

//val zioVersion = "2.0.0-M2"
val zioVersion = "1.0.11"
val korolevVersion = "1.0.0"
val neotypesVersion = "0.18.3"

lazy val bridgez = (project in file("."))
  .settings(
    name := "bridgez",

    libraryDependencies ++= Seq(
      "io.github.neotypes" %% "neotypes-core"       % neotypesVersion,
      "io.github.neotypes" %% "neotypes-zio"        % neotypesVersion,
      "io.github.neotypes" %% "neotypes-zio-stream" % neotypesVersion
    ).map(dep => dep.cross(CrossVersion.for3Use2_13)),

    libraryDependencies ++= Seq(
      "dev.zio" %% "zio"      % zioVersion,
      "dev.zio" %% "zio-test" % zioVersion % Test,

      "org.fomkin" %% "korolev"          % korolevVersion,
      "org.fomkin" %% "korolev-zio"      % korolevVersion,
      "org.fomkin" %% "korolev-zio-http" % korolevVersion
    ).map(dep => dep.exclude("org.scala-lang.modules", "scala-collection-compat_3")),

    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
