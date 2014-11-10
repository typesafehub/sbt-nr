package sbt.nr

import sbt._
import Keys._
import Def.Initialize

object SbtNrPlugin extends AutoPlugin {

  object autoImport {
    val NewRelic = config("newrelic").extend(Compile)

    val agentJar = SettingKey[String]("agent-jar")
    val configFile = SettingKey[String]("config-file")
    val environment = SettingKey[String]("environment")

    lazy val defaultNrSettings: Seq[Def.Setting[_]] = Seq(
      inTask(run)(Seq(runner <<= nrRunner)).head,

      mainClass in run <<= mainClass in run in Compile,

      unmanagedClasspath <<= unmanagedClasspath in Runtime,
      managedClasspath <<= managedClasspath in Runtime,
      internalDependencyClasspath <<= internalDependencyClasspath in Runtime,
      externalDependencyClasspath <<= Classpaths.concat(unmanagedClasspath, managedClasspath),
      dependencyClasspath <<= Classpaths.concat(internalDependencyClasspath, externalDependencyClasspath),
      exportedProducts <<= exportedProducts in Runtime,
      fullClasspath <<= Classpaths.concatDistinct(exportedProducts, dependencyClasspath),

      UIKeys.backgroundRunMain <<= SbtBackgroundRunPlugin.backgroundRunMainTask(fullClasspath, runner in run),
      UIKeys.backgroundRun <<= SbtBackgroundRunPlugin.backgroundRunTask(fullClasspath, mainClass in run, runner in run)
    )
  }

  import autoImport._

  def nrRunner: Initialize[Task[ScalaRun]] = Def.task {    
    if (agentJar.value == "" || configFile.value == "") throw new RuntimeException("You must provide agentJar and configFile settings.")

    val nrJavaOptions: Seq[String] = Seq(
      s"-javaagent:${agentJar.value}",
      s"-Dnewrelic.config.file=${configFile.value}",
      s"-Dnewrelic.environment=${environment.value}",
      "-Dnewrelic.enable.java.8")

    val forkConfig = ForkOptions(javaHome.value, outputStrategy.value, Seq.empty, Some(baseDirectory.value), nrJavaOptions, connectInput.value)
        
    if (fork.value) new ForkRun(forkConfig) else throw new RuntimeException("This plugin can only be run in forked mode")
  }

  override def requires = plugins.JvmPlugin

  override def trigger = allRequirements

  override val projectSettings = 
    Seq(
      agentJar := "",
      configFile := "",
      environment := "development") ++ 
    inConfig(NewRelic)(defaultNrSettings)
}
