import sbt._
import Keys._

object Dependencies {
  object V {
    val playVersion = "2.3.8"
    val sbtCoreNextVersion = "0.1.1"
    val junitVersion = "4.5"
    val junitInterface = "0.11"
  }

  def sbtPluginExtra(module:ModuleID):ModuleID =
    Defaults.sbtPluginExtra(module, "0.13", "2.10")

  val junit             = "junit" % "junit" % V.junitVersion % "test"
  val junitInterface    = "com.novocode" % "junit-interface" % V.junitInterface % "test"
  val playForkRunPlugin = sbtPluginExtra("com.typesafe.play" % "sbt-fork-run-plugin" % V.playVersion)
  val sbtCoreNextPlugin = sbtPluginExtra("org.scala-sbt" % "sbt-core-next" % V.sbtCoreNextVersion)
}