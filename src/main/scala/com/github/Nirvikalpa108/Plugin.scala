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
    //val motivationalQuotes: SettingKey[List[String]] = settingKey[List[String]]("a list of motivational quotes")
    //val voices: SettingKey[List[String]] = settingKey[List[String]]("a list of say voices")
    val speak = taskKey[Unit]("says nice motivational things")
    val voice = settingKey[String]("configure the voice") // voice that the user can set per sub-project
    val speakTestPassed = taskKey[Unit]("say something nice when the tests pass")
    val speakTestFailed = taskKey[Unit]("say something motivational when the tests fail")
    val speakTestError = taskKey[Unit]("say something motivational when the tests error")

   //lazy val speakTest = taskKey[Unit]("run tests and say something nice :)")
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
    // created sub projects
    // the tests are in the root of the project
    // they run no matter which sub project I am in
    // TODO: if I have tests in a sub-project, they're only run when I'm in that sub-project right?

    // when I'm in a sub-project, the right voice is used for the test
    // when I'm not in any sub-project (in the root)

    // very interesting ...
    // when I have only a sub project called root (is that a sub project or the project??!) -
    // I hear the global fallback AND the voice set for the root -
    // BUT when I have a second sub-project (or first, depending on whether root is a sub project) -
    // I only hear the voice for root and the second sub-project, NOT the global fall back
    // TODO: Is root sub project a reserved key word / means something?

    // when I'm in the root sub project, AND the tests are in the root, All of the sub-projects tests run in parallel
    // and therefore all of the voices are heard in parallel
    // TODO what can I do? Should I put the tests in the sub-projects? Will this counter that?

    // ***** IMPORTANT, EXPECTED BEHAVIOUR HERE *****
    // when I'm in a sub-project AND the tests are in the root, I only hear the sub-project voice
    // when I'm in a sub-project AND no voice has been set in the sub-proj AND the tests are in the root, I hear the global fallback set by the plugin

    //setting the voice to the narrowest scoping within the tasks, so build users have max flexibility and can set per sub-project
    speakTestPassed := {
      val v = (speakTestPassed / voice).value
      Process(s"say -v $v well done, your tests passed").!!
    },
    speakTestFailed := {
      val v = (speakTestFailed / voice).value
      Process(s"say -v $v try again, better luck next time").!!
    },
    speakTestError := {
      val v = (speakTestError / voice).value
      Process(s"say -v $v oh no, there's been an error with your tests. Let's see what's wrong.").!!
    },

    // execute speak in the sbt shell to see this working
    speak := {
      val output = speakTestOutcomeDynamic.value
      output
    }
  )
}
