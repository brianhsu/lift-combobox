val liftVersion = SettingKey[String]("liftVersion", "Version number of the Lift Web Framework")

name := "combobox"
version := "3.0-M2-SNAPSHOT"
organization := "net.liftmodules"
crossScalaVersions := Seq("2.9.2", "2.9.1-1", "2.9.1")
scalaVersion := "2.11.2"
liftVersion := "3.0-M2"
scalacOptions ++= Seq("-unchecked", "-deprecation")

scalacOptions in (Compile, doc) ++= Seq(
  "-doc-root-content", "README.scaladoc",
   "-doc-title", "Lift ComboBox Module 2.5-RC-0.6"
)

libraryDependencies ++= Seq(
  "net.liftweb" %% "lift-webkit" % liftVersion.value % "provided",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test"
)

publishTo := Some(
  Resolver.sftp("bone", "bone.twbbs.org.tw", "public_html/ivy")
)
