package com.github.Nirvikalpa108

import sbt.AutoPlugin
import sbt._

object MotivationPlugin extends AutoPlugin {

  override def requires = sbt.plugins.CorePlugin
  override def trigger = allRequirements

  object autoImport {
    val motivations: SettingKey[List[String]] = settingKey[List[String]]("a list of motivational quotes")
  }

  import autoImport._
  override lazy val globalSettings: Seq[Setting[_]] = List(motivations := List(
  "Take a deep breath in and out",
  "You birthed a baby completely by yourself, you can do anything",
  "You took Zak through sleep training when everyone was against you, you can do anything",
  "You barely slept for the first 5 months of Zak’s life. This is easy compared to that.",
  "You can achieve anything you want, just take it step by step",
  "Where you are is where you are meant to be",
  "You have everything you need to look after yourself",
  "I believe in you",
  )
  )

}
