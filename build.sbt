lazy val V = _root_.scalafix.sbt.BuildInfo
inThisBuild(
  List(
    organization := "com.typesafe.play.contrib",
    scalaVersion := V.scala211,
    crossScalaVersions := Seq(V.scala211, V.scala212),
    addCompilerPlugin(scalafixSemanticdb),
    scalacOptions ++= List("-Yrangepos"),
    updateOptions := updateOptions.value.withLatestSnapshots(false),
    credentials += Credentials(Path.userHome / ".sbt" / "credentials"),
    publishTo := {
      val nexus = "https://nexus.foyer.lu/repository/"
      if (version.value.trim.endsWith("SNAPSHOT")) {
        Some("snapshots" at nexus + "mvn-hosted-snapshots")
      } else {
        Some("releases" at nexus + "mvn-hosted-releases")
      }
    }
  )
)

skip in publish := true

lazy val play25 = "2.5.19"
lazy val play26 = "2.6.22"
lazy val play27 = "2.7.4"
lazy val play28 = "2.8.0"

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

lazy val input27 = project
  .in(file("play-v2.7.x-to-v2.8.x/input"))
  .settings(skip in publish := true)
  .settings(
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.4",
      "org.scalamock" %% "scalamock" % "4.1.0",
      "org.scalacheck" %% "scalacheck" % "1.11.5"
    )
  )

lazy val output28 = project
  .in(file("play-v2.7.x-to-v2.8.x/output"))
  .settings(skip in publish := true)
  .settings(
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.1.0",
      "org.scalatestplus" %% "scalatestplus-scalacheck" % "3.1.0.0-RC2",
      "org.scalamock" %% "scalamock" % "4.4.0",
      "org.scalacheck" %% "scalacheck" % "1.14.1"
    )
  )

lazy val rules28 = project
  .in(file("play-v2.7.x-to-v2.8.x/rules"))
  .dependsOn(core)
  .settings(
    moduleName := "play-migrations-v27-to-v28-scalafix-rules",
    libraryDependencies += "ch.epfl.scala" %% "scalafix-core" % V.scalafixVersion
  )

lazy val tests28 = project
  .in(file("play-v2.7.x-to-v2.8.x/tests"))
  .settings(
    skip in publish := true,
    libraryDependencies += "ch.epfl.scala" % "scalafix-testkit" % V.scalafixVersion % Test cross CrossVersion.full,
    compile.in(Compile) :=
      compile.in(Compile).dependsOn(compile.in(input27, Compile)).value,
    scalafixTestkitOutputSourceDirectories :=
      sourceDirectories.in(output28, Compile).value,
    scalafixTestkitInputSourceDirectories :=
      sourceDirectories.in(input27, Compile).value,
    scalafixTestkitInputClasspath :=
      fullClasspath.in(input27, Compile).value
  )
  .dependsOn(rules28)
  .enablePlugins(ScalafixTestkitPlugin)
