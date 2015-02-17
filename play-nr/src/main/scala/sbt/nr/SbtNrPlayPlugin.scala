package sbt.nr

import sbt._
import Keys._
import play.Play
import sbt.plugins.BackgroundRunPlugin
import sbt.BackgroundJobServiceKeys
import play.sbt.forkrun.{ PlayForkRun, PlayForkOptions }
import play.forkrun.protocol.ForkConfig
import PlayForkRun.autoImport._

object SbtNrPlayPlugin extends AutoPlugin {
  import SbtNrPlugin.autoImport._

  object autoImport {
    val NewRelicPlay = config("newrelicplay").extend(NewRelic)
  }

  import autoImport._

  override def trigger = AllRequirements

  override def requires = SbtNrPlugin && Play && PlayForkRun && BackgroundRunPlugin

  lazy val defaultNrPlaySettings: Seq[Def.Setting[_]] = {
    Seq(
      javaOptions <++= SbtNrPlugin.javaOptions,
      PlayForkRunKeys.playForkOptions <<= nrPlayForkOptionsTask,
      BackgroundJobServiceKeys.backgroundRun in ThisProject <<= PlayForkRun.backgroundForkRunTask)
  }

  def nrPlayForkOptionsTask: Def.Initialize[Task[PlayForkOptions]] = Def.task {
    val in = (PlayForkRunKeys.playForkOptions in ThisProject).value
    val jo = (javaOptions in NewRelicPlay).value
    in.copy(jvmOptions = in.jvmOptions ++ jo)
  }

  override def projectSettings = inConfig(NewRelicPlay)(defaultNrPlaySettings) ++ SbtNrPlugin.projectSettings
}