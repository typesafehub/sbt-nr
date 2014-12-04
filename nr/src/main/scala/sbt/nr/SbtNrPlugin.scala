package sbt.nr

import sbt._
import Keys._
import Def.Initialize
import scala.collection.mutable.ArrayBuffer
import java.io.File

object SbtNrPlugin extends AutoPlugin {

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

  object autoImport {
    val NewRelic = config("newrelic").extend(Compile)
    val newRelicAgentJar = SettingKey[String]("NewRelic agent jar file.")
    val newRelicConfigFile = SettingKey[String]("NewRelic configuration file, see https://docs.newrelic.com/docs/agents/java-agent/configuration/java-agent-configuration-config-file for documentation.")
    val newRelicEnvironment = SettingKey[String]("NewRelic run environment, see https://docs.newrelic.com/docs/apm/new-relic-apm/maintenance/connecting-hosts-your-account#environments for documentation.")
  }

  import autoImport._

  private[nr] def verifySettings(jarFilePath: String, configFilePath: String) = {
    def exists(path: String): Boolean = (new File(path)).exists

    val errorMessages = ArrayBuffer.empty[String]

    if (jarFilePath == "") {
      errorMessages += "Set 'newRelicAgentJar in NewRelic := \"<filename>\"' in your build to the location of the New Relic agent jar file."
    } else if (!exists(jarFilePath)) {
      errorMessages += "The specified file '" + jarFilePath + 
        "' does not exist. Please make sure the location is correctly set with 'newRelicAgentJar in NewRelic := \"<filename>\"'"
    }

    if (configFilePath == "") {
      errorMessages += "Create a configuration file for New Relic and set 'newRelicConfigFile := \"<filename>\"' in your build."
    } else if (!exists(configFilePath)){
      errorMessages += "The specified file '" + configFilePath + 
        "' does not exist. Please make sure the location is correctly set with 'newRelicConfigFile in NewRelic := \"<filename>\"'" 
    }
  
    if (errorMessages.size > 0) {
      throw new RuntimeException(errorMessages.mkString("\n"))
    } 
  }

  def nrRunner: Initialize[Task[ScalaRun]] = Def.task {        
    verifySettings(newRelicAgentJar.value, newRelicConfigFile.value)

    val nrJavaOptions: Seq[String] = Seq(
      s"-javaagent:${newRelicAgentJar.value}",
      s"-Dnewrelic.config.file=${newRelicConfigFile.value}",
      s"-Dnewrelic.environment=${newRelicEnvironment.value}",
      "-Dnewrelic.enable.java.8")

    val forkConfig = ForkOptions(javaHome.value, outputStrategy.value, Seq.empty, Some(baseDirectory.value), nrJavaOptions, connectInput.value)
        
    if (fork.value) new ForkRun(forkConfig) else throw new RuntimeException("This plugin can only be run in forked mode")
  }

  override def requires = SbtUIPlugin

  override def trigger = allRequirements

  override val projectSettings = 
    Seq(
      newRelicAgentJar := "",
      newRelicConfigFile := "",
      newRelicEnvironment := "development") ++ 
    inConfig(NewRelic)(defaultNrSettings)
}
