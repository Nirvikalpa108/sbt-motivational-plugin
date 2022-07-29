package com.github.Nirvikalpa108

import sbt.Keys.executeTests
import sbt.{AutoPlugin, Test, TestResult, *}

object MotivationPlugin extends AutoPlugin {
  // this specifies the plugins that I need to depend on. Autoplugin puts the plugin dependency settings in the right
  // order for us. This has to be done manually before 0.13.5.
 // override def requires = sbt.plugins.CorePlugin
  override def requires = sbt.plugins.JvmPlugin // changing to hook onto compile. Allows this plugin to come after jvm plugins
  // this means the plugin is automatically added to the project's which use it. no manual trigger is required.
  override def trigger = allRequirements

  object autoImport {
    val speak = taskKey[Unit]("says nice motivational things")
    val voice = settingKey[String]("configure the voice") // voice that the user can set per sub-project
    val speakTestPassed = taskKey[Unit]("say something nice when the tests pass")
    val speakTestFailed = taskKey[Unit]("say something motivational when the tests fail")
    val speakTestError = taskKey[Unit]("say something motivational when the tests error")

    val speakTestOutcomeDynamic = Def.taskDyn {
      (Test / executeTests).value.overall match {
        case TestResult.Passed => Def.task(speakTestPassed.value)
        case TestResult.Failed => Def.task(speakTestFailed.value)
        case TestResult.Error => Def.task(speakTestError.value)
      }
    }
  }

  import autoImport.*
  import sys.process.*
  override lazy val globalSettings: Seq[Setting[_]] = List(
    voice := "Daniel", // voice is set to a default value in global settings
  )
  override lazy val projectSettings: Seq[Setting[_]] = List(
    //setting the voice to the narrowest scoping within the tasks,
    //so build users have max flexibility and can set per sub-project
    speakTestPassed := Process(s"say -v ${getVoice(speakTestPassed).value} well done.").!!,
    speakTestFailed := Process(s"say -v ${getVoice(speakTestFailed).value} better luck next time").!!,
    speakTestError := Process(s"say -v ${getVoice(speakTestError).value} there's been an error with your tests.").!!,
    // execute speak in the sbt shell to see this working
    speak := {
      val output = speakTestOutcomeDynamic.value
      output
    }
  )
  def getVoice(t: TaskKey[Unit]): SettingKey[String] = t / voice
  def say(voice: String): Unit = Process(s"say -v $voice testing testing one two three").!!
}
