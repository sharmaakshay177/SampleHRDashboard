val ScalatraVersion = "2.7.1"

ThisBuild / scalaVersion := "2.13.4"
ThisBuild / organization := "com.github.sharmaakshay177"

lazy val hello = (project in file("."))
  .settings(
    name := "HumanResourceDashboardTemplate",
    version := "0.1.0-SNAPSHOT",

    libraryDependencies ++= Seq(
      "org.scalatra" %% "scalatra" % ScalatraVersion,
      "org.scalatra" %% "scalatra-scalatest" % ScalatraVersion % "test",
      "ch.qos.logback" % "logback-classic" % "1.2.3" % "runtime",
      "org.eclipse.jetty" % "jetty-webapp" % "9.4.35.v20201120" % "container",
      "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided",
      "org.scalatra" %% "scalatra-json" % "2.7.1",
      "mysql" % "mysql-connector-java" % "5.1.16",
      "org.json4s" %% "json4s-jackson" % "3.6.10",
      "org.scalatra" %% "scalatra-swagger" % "2.7.1",
      "org.yaml" % "snakeyaml" % "1.27",
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.12.1",
      "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % "2.12.1",
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.12.1",
      "org.scalamock" %% "scalamock" % "5.1.0" % Test,
      "org.scalatest" %% "scalatest" % "3.2.4" % Test
    ),
  )

enablePlugins(SbtTwirl)
enablePlugins(JettyPlugin)
