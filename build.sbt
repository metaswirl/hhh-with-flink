lazy val commonSettings = Seq(
  organization := "berlin.bbdc.inet",
  version := "0.1.0",
  unmanagedBase := file(".") / "lib",
  libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test"
)
lazy val noScala = Seq(
  autoScalaLibrary := false, // drop scala dep
  crossPaths := false, // drop scala version number
  javacOptions in (Compile, compile) ++= Seq("-source", "1.8", "-target", "1.8", "-g:lines")
)

lazy val pcap2flink_standalone = (project in file(".")).
  settings(commonSettings: _*).
  settings(noScala: _*).
  settings(
    name := "pcap2flink_standalone",
    mainClass in Compile := Some("berlin.bbdc.inet.pcap2flink_standalone.FlinkJob")).
  dependsOn(exceptions)

lazy val exceptions = (project in file("exceptions")).
  settings(commonSettings: _*).
  settings(noScala: _*).
  settings(name := "exceptions")
