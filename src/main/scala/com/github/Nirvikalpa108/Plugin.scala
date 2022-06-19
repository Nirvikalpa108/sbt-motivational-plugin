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
//    val voice = settingKey[String]("configure the voice") // voice that the user can set per sub-project
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

//    val dynamic = Def.taskDyn {
//      // decide what to evaluate based on the value of Test / executeTests
//      if((Test / executeTests).value.overall == TestResult.Failed)
//      // this is only evaluated if the tests fail
//        Def.task {
//          speakTestFailed.value
//        }
//      else
//      // only evaluated if the tests do not fail (this is wrong, because of course they could error, but anyway!)
//        Def.task {
//          speakTestPassed.value
//        }
//    }
  }

  import autoImport.*
  import sys.process.*
  override lazy val globalSettings: Seq[Setting[_]] = List(

    //voice := "Daniel", // voice is set to a default value in global settings
//    motivationalQuotes := MotivationalQuotes.quotes,
//    voices := Voices.allVoices,
//    speak := {
//      val m = motivationalQuotes.value
//      val v = voices.value
//      val random = new Random
//      val name = System.getProperty("user.name")
//      // I don't like these voices I've chosen, so leaving this for now until I have time to change them
//      //Process(s"say -v ${v(random.nextInt(v.length))} ${m(random.nextInt(m.length))} $name").!!
//      Process(s"say ${m(random.nextInt(m.length))} $name").!!
//    },
  )
  override lazy val projectSettings: Seq[Setting[_]] = List(
    // execute speak in the sbt shell to see this working
    speakTestFailed := Process("say try again. You'll have better luck next time").!!,
    speakTestPassed := Process("say well done, your tests passed").!!,
    speakTestError := Process("say oops something went wrong. You're still amazing though!").!!,

    speak := {
      val output = speakTestOutcomeDynamic.value
      output
    }
//    speakTest := speakTestOutcomeDynamic.value,

//    Test / executeTests := { // := is a re-wiring operator
//      val old = (Test / executeTests).value // this makes the happens-before relationship to the original task
//      speak.value // happens before for the speak task
//      old
//    },
    // setting the voice to the narrowest scoping within the tasks, so build users have max flexibility and can set per sub-project
//    speakTestPassed := {
//      val v = (speakTestPassed / voice).value
//      Process(s"say -v $v well done, your tests passed").!!
//    },
//    speakTestFailed := {
//      val v = (speakTestFailed / voice).value
//      Process(s"say -v $v try again, better luck next time").!!
//    },
//    speakTestError := {
//      val v = (speakTestError / voice).value
//      Process(s"say -v $v oh no, there's been an error with your tests. Let's see what's wrong.").!!
//    },
//
  )
}
