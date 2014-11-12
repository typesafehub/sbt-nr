package sbt.nr

import sbt._
import Keys._
import Def.Initialize

object SbtNrPlugin extends AutoPlugin {

  object autoImport {
    val NewRelic = config("newrelic").extend(Compile)

    val newRelicAgentJar = SettingKey[String]("NewRelic agent jar file.")
    val newRelicConfigFile = SettingKey[String]("NewRelic configuration file, see https://docs.newrelic.com/docs/agents/java-agent/configuration/java-agent-configuration-config-file for documentation.")
    val newRelicEnvironment = SettingKey[String]("NewRelic run environment, see https://docs.newrelic.com/docs/apm/new-relic-apm/maintenance/connecting-hosts-your-account#environments for documentation.")

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

  private def nrRunner: Initialize[Task[ScalaRun]] = Def.task {    
    
    val errorMsg = 
      if (newRelicAgentJar.value == "") {
        Some("Set 'newRelicAgentJar in NewRelic := \"<filename>\"' in your build to the location of the New Relic agent jar file.")
      } else if (newRelicConfigFile.value == "") {
        Some("Create a configuration file for New Relic and set 'newRelicConfigFile := \"<filename>\"' in your build.")
      } else None

    errorMsg match {
      case Some(error) => throw new RuntimeException(error)
      case None => // all good 
    }

    val nrJavaOptions: Seq[String] = Seq(
      s"-javaagent:${newRelicAgentJar.value}",
      s"-Dnewrelic.config.file=${newRelicConfigFile.value}",
      s"-Dnewrelic.environment=${newRelicEnvironment.value}",
      "-Dnewrelic.enable.java.8")

    val forkConfig = ForkOptions(javaHome.value, outputStrategy.value, Seq.empty, Some(baseDirectory.value), nrJavaOptions, connectInput.value)
        
    if (fork.value) new ForkRun(forkConfig) else throw new RuntimeException("This plugin can only be run in forked mode")
  }

  override def requires = plugins.JvmPlugin

  override def trigger = allRequirements

  override val projectSettings = 
    Seq(
      newRelicAgentJar := "",
      newRelicConfigFile := "",
      newRelicEnvironment := "development") ++ 
    inConfig(NewRelic)(defaultNrSettings)
}
