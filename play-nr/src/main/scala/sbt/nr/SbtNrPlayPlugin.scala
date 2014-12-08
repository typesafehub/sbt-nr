package sbt.nr

import sbt._
import Keys._
import play.{Play, PlayInternalKeys}

object SbtNrPlayPlugin extends AutoPlugin with PlayInternalKeys {
  import SbtNrPlugin.{autoImport => BaseKeys, verifySettings}

  override def trigger = allRequirements

  override def requires = Play && SbtNrPlugin

  object autoImport {
    val newRelicPlayRunner = taskKey[BackgroundJobHandle]("Run play dev-mode runner with NewRelic agent")
  }

  import autoImport._

  def newRelicPlayRunnerTask(newRelicAgentJar:TaskKey[String], newRelicConfigFile:TaskKey[String], newRelicEnvironment:TaskKey[String]):Def.Initialize[Task[BackgroundJobHandle]] = Def.task {
    val jar = newRelicAgentJar.value
    val config = newRelicConfigFile.value
    val env = newRelicEnvironment.value
    val runner = playBackgroundRunTaskBuilder.value

    println(s" -- jar: $jar")
    println(s" -- config: $config")
    println(s" -- env: $env")
    println(s" -- runner: $runner")

    verifySettings(jar, config)

    val nrJavaOptions: Seq[String] = Seq(
      s"-javaagent:${jar}",
      s"-Dnewrelic.config.file=${config}",
      s"-Dnewrelic.environment=${env}",
      "-Dnewrelic.enable.java.8")

    println(s" -- options: $nrJavaOptions")

    runner(nrJavaOptions)
  }

  lazy val defaultNrSettings: Seq[Def.Setting[_]] =
    Seq(
      newRelicPlayRunner <<= newRelicPlayRunnerTask(BaseKeys.newRelicAgentJar,BaseKeys.newRelicConfigFile,BaseKeys.newRelicEnvironment)
    ) ++ inConfig(BaseKeys.NewRelic)(Seq(
      UIKeys.backgroundRunMain in ThisProject := newRelicPlayRunner.value,
      UIKeys.backgroundRun in ThisProject := newRelicPlayRunner.value
    ))

  override val projectSettings = SbtNrPlugin.projectSettings ++ defaultNrSettings
}