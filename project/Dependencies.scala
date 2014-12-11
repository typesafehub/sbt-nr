import sbt._
import Keys._

object Dependencies {
  def playPlugin = 
  	Defaults.sbtPluginExtra("com.typesafe.play" % "sbt-plugin" % "2.3.8-2de45b3774b6757f4aae980f8b5b152c1d2b73a5", "0.13", "2.10")

  def uiPlugin = 
  	Defaults.sbtPluginExtra("com.typesafe.sbtrc" % "ui-interface-0-13" % "1.0-43891de56b625f1c0e810348360fee05a22445bf", "0.13", "2.10")
}