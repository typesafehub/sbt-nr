package sbt.nr

import sbt._
import Keys._
import play.PlayInternalKeys

object SbtNrPlayPlugin extends AutoPlugin with PlayInternalKeys {
  import SbtNrPlugin._

  override def trigger = AllRequirements

  override def requires = SbtNrPlugin

  lazy val defaultNrSettings: Seq[Def.Setting[_]] = Seq(
    inTask(run)(Seq(runner <<= SbtNrPlugin.nrRunner)).head,

    UIKeys.backgroundRunMain in ThisProject := playBackgroundRunTaskBuilder.value((Keys.javaOptions in Runtime).value),

    UIKeys.backgroundRun in ThisProject := playBackgroundRunTaskBuilder.value((Keys.javaOptions in Runtime).value)
  )
}