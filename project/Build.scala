import sbt._
import Keys._
import com.typesafe.sbt.SbtGit._

object SbtNrBuild extends Build {
  def baseVersions: Seq[Setting[_]] = versionWithGit

  lazy val sbtNr = Project(
    id = "sbt-nr",
    base = file("."),
    settings = defaultSettings ++ noPublishSettings,
    aggregate = Seq(sbtNrMain, sbtNrPlay)
  )

  lazy val sbtNrMain = Project(
    id = "sbt-nr-main",
    base = file("nr"),
    settings = defaultSettings ++ Seq(
      name := "sbt-nr-main",
      libraryDependencies ++= Seq(Dependencies.sbtCoreNextPlugin)
    )
  )

  lazy val sbtNrPlay = Project(
    id = "sbt-nr-play",
    base = file("play-nr"),
    dependencies = Seq(sbtNrMain),
    settings = defaultSettings ++ Seq(
      name := "sbt-nr-play",
      libraryDependencies ++= Seq(Dependencies.playForkRunPlugin)
    )
  )

  lazy val typesafeIvyReleases = Resolver.url("Typesafe Ivy Releases Repo", new URL("http://repo.typesafe.com/typesafe/releases/"))(Resolver.ivyStylePatterns)

  lazy val typesafeIvySnapshots = Resolver.url("Typesafe Ivy Snapshots Repo", new URL("http://private-repo.typesafe.com/typesafe/ivy-snapshots/"))(Resolver.ivyStylePatterns)

  lazy val defaultSettings: Seq[Setting[_]] = Defaults.defaultSettings ++ baseVersions ++ Seq(
    sbtPlugin := true,
    organization := "com.typesafe.sbtnr",
    version <<= version in ThisBuild,
    publishMavenStyle := false,
    publishTo := Some(typesafeIvySnapshots),
    resolvers += typesafeIvyReleases,
    libraryDependencies ++= Seq(Dependencies.junit, Dependencies.junitInterface)
  )

  lazy val noPublishSettings: Seq[Setting[_]] = Seq(
    publish := {},
    publishLocal := {}
  )
}
