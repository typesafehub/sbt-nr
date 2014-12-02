import sbt._
import Keys._

object Dependencies {
  def playPlugin = Defaults.sbtPluginExtra("com.typesafe.play" % "sbt-plugin" % "2.3.8-7591330ccd513dfef0a95faf605197b852c3940a", "0.13", "2.10")
  def uiPlugin = Defaults.sbtPluginExtra("com.typesafe.sbtrc" % "ui-interface-0-13" % "1.0-43891de56b625f1c0e810348360fee05a22445bf", "0.13", "2.10")
}