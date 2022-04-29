lazy val root = (project in file("."))
  .settings(
    name := "motivation-plugin",
    scalaVersion := "2.12.15",
    version := "0.1.0-SNAPSHOT",
    organization := "com.github.Nirvikalpa108",
    sbtPlugin := true,
    libraryDependencies ++= Seq(),
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
