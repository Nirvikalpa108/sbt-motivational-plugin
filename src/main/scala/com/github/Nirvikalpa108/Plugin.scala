package com.github.Nirvikalpa108

import sbt.AutoPlugin
import sbt._

object MotivationPlugin extends AutoPlugin {
  // this specifies the plugins that I need to depend on. Autoplugin puts the plugin dependency settings in the right
  // order for us. This has to be done manually before 0.13.5.
  override def requires = sbt.plugins.CorePlugin
  // this means the plugin is automatically added to the project's which use it. no manual trigger is required.
  override def trigger = allRequirements

  object autoImport {
    val motivationalQuotes: SettingKey[List[String]] = settingKey[List[String]]("a list of motivational quotes")
    val speak = taskKey[Unit]("says nice motivational things")
  }

  import autoImport._
  import sys.process._
  override lazy val globalSettings: Seq[Setting[_]] = List(
    motivationalQuotes := MotivationalQuotes.quotes,
    speak := {
      val m = motivationalQuotes.value
      val name = System.getProperty("user.name")
      Process(s"say ${m.head} $name").!!
    }
  )
}
