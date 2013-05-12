name := "combobox"

version := "2.6-M2-0.7"

organization := "net.liftmodules"
 
scalaVersion := "2.10.0"

crossScalaVersions := Seq("2.9.2", "2.9.1-1", "2.9.1")

scalacOptions ++= Seq("-unchecked", "-deprecation")

scalacOptions in (Compile, doc) ++= Seq(
  "-doc-root-content", "README.scaladoc",
   "-doc-title", "Lift ComboBox Module 2.5-RC-0.6"
)

libraryDependencies ++= Seq(
  "net.liftweb" %% "lift-webkit" % "2.6-M2",
  "org.scalatest" %% "scalatest" % "1.9.1" % "test"
)

publishTo := Some(
  Resolver.sftp("bone", "bone.twbbs.org.tw", "public_html/ivy")
)
