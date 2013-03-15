name := "combobox"

version := "2.5-RC2-0.3"

organization := "net.liftmodules"
 
scalaVersion := "2.10.0"

crossScalaVersions := Seq("2.9.2", "2.9.1-1", "2.9.1")

scalacOptions ++= Seq("-unchecked", "-deprecation")

scalacOptions in (Compile, doc) ++= Seq(
  "-doc-root-content", "README.scaladoc",
   "-doc-title", "Lift ComboBox Module 2.5-RC-0.3"
)

docDirectory in Compile <<= (baseDirectory / "api")

libraryDependencies ++= Seq(
  "net.liftweb" %% "lift-webkit" % "2.5-RC2",
  "org.scalatest" %% "scalatest" % "1.9.1" % "test"
)

publishTo := Some(
  Resolver.sftp("bone", "bone.twbbs.org.tw", "public_html/ivy")
)
