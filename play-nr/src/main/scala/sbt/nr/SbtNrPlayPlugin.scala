package sbt.nr

import sbt._
import Keys._
import play.{Play, PlayInternalKeys}

object SbtNrPlayPlugin extends AutoPlugin with PlayInternalKeys {
  import SbtNrPlugin.autoImport._
  
  object autoImport {
    val NewRelicPlay = config("newrelicplay").extend(NewRelic)
    val nrPlayRunner = taskKey[BackgroundJobHandle]("Run play dev-mode runner with NewRelic agent")
  }

  import autoImport._

  override def trigger = AllRequirements

  override def requires = SbtNrPlugin && Play

  lazy val defaultNrPlaySettings: Seq[Def.Setting[_]] = {
    Seq(nrPlayRunner <<= nrPlayRunnerTask) ++ 
    Seq(
      UIKeys.backgroundRunMain in ThisProject := nrPlayRunner.value,
      UIKeys.backgroundRun in ThisProject := nrPlayRunner.value)
  }

  def nrPlayRunnerTask: Def.Initialize[Task[BackgroundJobHandle]] = Def.task {
    playBackgroundRunTaskBuilder.value(SbtNrPlugin.javaOptions.value)
  }

  override def projectSettings = inConfig(NewRelicPlay)(defaultNrPlaySettings) ++ SbtNrPlugin.projectSettings
}