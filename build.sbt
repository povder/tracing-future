import Dependencies._

lazy val commonSettings = Vector(
  version := "1.0.0",
  scalaVersion := "2.12.1"
)

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    name := "tracing-future-demo"
  ).aggregate(tracing, notracing, agent, snippets)

lazy val tracing = (project in file("tracing"))
  .settings(commonSettings: _*)
  .settings(
    name := "tracing",
    libraryDependencies += sourcecode
  )

lazy val notracing = (project in file("notracing"))
  .settings(commonSettings: _*)
  .settings(
    name := "notracing"
  )

lazy val agent = (project in file("notracing"))
  .settings(commonSettings: _*)
  .settings(
    name := "agent",
    target := baseDirectory.value / "target_agent",
    fork := true,
    javaOptions += s"-javaagent:${baseDirectory.value}/agent_2.12.jar"
  )

lazy val snippets = (project in file("snippets"))
  .settings(commonSettings: _*)
  .settings(
    name := "snippets"
  ).dependsOn(tracing)
