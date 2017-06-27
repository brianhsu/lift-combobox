val liftVersion = SettingKey[String]("liftVersion", "Version number of the Lift Web Framework")
val liftEdition = SettingKey[String]("liftEdition", "Lift Edition (short version number to append to artifact name)")

name := "combobox"
moduleName := name.value + "_" + liftEdition.value
version := "0.6-SNAPSHOT"
organization := "net.liftmodules"
crossScalaVersions := Seq("2.11.11", "2.12.2")
scalaVersion := crossScalaVersions.value.head
liftVersion <<= liftVersion ?? "3.0.1"
liftEdition := liftVersion.value.replaceAllLiterally("-SNAPSHOT", "").split('.').take(2).mkString(".")
scalacOptions ++= Seq("-unchecked", "-deprecation")

scalacOptions in (Compile, doc) ++= Seq(
  "-doc-root-content", "README.scaladoc",
   "-doc-title", "Lift ComboBox Module " + liftEdition.value + "_" + scalaBinaryVersion.value + "-" + version.value
)

libraryDependencies ++= Seq(
  "net.liftweb" %% "lift-webkit" % liftVersion.value % "provided",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)

publishTo := Some(
  Resolver.sftp("bone", "bone.twbbs.org.tw", "public_html/ivy")
)
