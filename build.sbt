import com.typesafe.sbt.SbtGit._
import scalariform.formatter.preferences._

sbtPlugin := true

name := "sbt-nr"

organization := "com.typesafe.sbt"

// GIT
versionWithGit

// SCALARIFORM
scalariformSettings

// SBT UI 
libraryDependencies += Defaults.sbtPluginExtra("com.typesafe.sbtrc" % "ui-interface-0-13" % "1.0-43891de56b625f1c0e810348360fee05a22445bf", "0.13", "2.10")
