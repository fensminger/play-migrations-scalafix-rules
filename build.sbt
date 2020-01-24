lazy val V = _root_.scalafix.sbt.BuildInfo
inThisBuild(
  List(
    organization := "com.typesafe.play.contrib",
    scalaVersion := V.scala211,
    crossScalaVersions := Seq(V.scala211, V.scala212),
    addCompilerPlugin(scalafixSemanticdb),
    scalacOptions ++= List("-Yrangepos"),
    updateOptions := updateOptions.value.withLatestSnapshots(false)
  )
)

skip in publish := true

lazy val play25 = "2.5.19"
lazy val play26 = "2.6.22"

lazy val core = project
  .settings(
    moduleName := "play-migrations-core",
    libraryDependencies += "ch.epfl.scala" %% "scalafix-core" % V.scalafixVersion
  )

// migrations from v2.5.x to v2.6.x

lazy val rules26 = project
  .in(file("play-v2.5.x-to-v2.6.x/rules"))
  .dependsOn(core)
  .settings(
    moduleName := "play-migrations-v25-to-v26-scalafix-rules",
    libraryDependencies += "ch.epfl.scala" %% "scalafix-core" % V.scalafixVersion
  )

lazy val input25 = project
  .in(file("play-v2.5.x-to-v2.6.x/input"))
  .settings(skip in publish := true)
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play" % play25,
      "com.typesafe.play" %% "play-ws" % play25
    )
  )

lazy val output26 = project
  .in(file("play-v2.5.x-to-v2.6.x/output"))
  .settings(skip in publish := true)
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play" % play26,
      "com.typesafe.play" %% "play-ws" % play26
    )
  )

lazy val tests26 = project
  .in(file("play-v2.5.x-to-v2.6.x/tests"))
  .settings(
    skip in publish := true,
    libraryDependencies += "ch.epfl.scala" % "scalafix-testkit" % V.scalafixVersion % Test cross CrossVersion.full,
    compile.in(Compile) :=
      compile.in(Compile).dependsOn(compile.in(input25, Compile)).value,
    scalafixTestkitOutputSourceDirectories :=
      sourceDirectories.in(output26, Compile).value,
    scalafixTestkitInputSourceDirectories :=
      sourceDirectories.in(input25, Compile).value,
    scalafixTestkitInputClasspath :=
      fullClasspath.in(input25, Compile).value
  )
  .dependsOn(rules26)
  .enablePlugins(ScalafixTestkitPlugin)

lazy val inputScalatest30 = project
  .in(file("scalatest-v3.0.x-to-v3.1.x/input"))
  .settings(skip in publish := true)
  .settings(
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.4",
      "org.scalamock" %% "scalamock" % "4.1.0",
      "org.scalacheck" %% "scalacheck" % "1.11.5"
    )
  )

lazy val inputScalatest31 = project
  .in(file("scalatest-v3.0.x-to-v3.1.x/output"))
  .settings(skip in publish := true)
  .settings(
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.1.0",
      "org.scalatestplus" %% "scalatestplus-scalacheck" % "3.1.0.0-RC2",
      "org.scalamock" %% "scalamock" % "4.4.0",
      "org.scalacheck" %% "scalacheck" % "1.14.1"
    )
  )

lazy val rulesScalatest31 = project
  .in(file("scalatest-v3.0.x-to-v3.1.x/rules"))
  .dependsOn(core)
  .settings(
    moduleName := "scalatest-v3.0.x-to-v3.1.x-rules",
    libraryDependencies += "ch.epfl.scala" %% "scalafix-core" % V.scalafixVersion
  )

lazy val testsScalatest31 = project
  .in(file("scalatest-v3.0.x-to-v3.1.x/tests"))
  .settings(
    skip in publish := true,
    libraryDependencies += "ch.epfl.scala" % "scalafix-testkit" % V.scalafixVersion % Test cross CrossVersion.full,
    compile.in(Compile) :=
      compile.in(Compile).dependsOn(compile.in(inputScalatest30, Compile)).value,
    scalafixTestkitOutputSourceDirectories :=
      sourceDirectories.in(inputScalatest31, Compile).value,
    scalafixTestkitInputSourceDirectories :=
      sourceDirectories.in(inputScalatest30, Compile).value,
    scalafixTestkitInputClasspath :=
      fullClasspath.in(inputScalatest30, Compile).value
  )
  .dependsOn(rulesScalatest31)
  .enablePlugins(ScalafixTestkitPlugin)
