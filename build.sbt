name := "toggl-cli"

version := "0.1"

scalaVersion := "2.11.4"

libraryDependencies ++= Seq(
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.2",
  "org.scalaj" % "scalaj-time_2.10.2" % "0.7",
  "io.argonaut" %% "argonaut" % "6.0.4"
)


