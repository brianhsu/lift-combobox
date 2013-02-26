name := "combobox"

liftVersion <<= liftVersion ?? "2.5-RC1"

version <<= liftVersion apply { _ + "-0.2" }

organization := "net.liftmodules"
 
scalaVersion := "2.10.0"

crossScalaVersions := Seq("2.9.2", "2.9.1-1", "2.9.1")

scalacOptions ++= Seq("-unchecked", "-deprecation")

scalacOptions in (Compile, doc) ++= Opts.doc.title("Lift ComboBox Module") 

resolvers ++= Seq(
    "Scala-Tools" at "https://oss.sonatype.org/content/repositories/snapshots/"
)

libraryDependencies <++= liftVersion { v =>
  "net.liftweb" %% "lift-webkit" % v % "compile->default" :: Nil
}    

publishTo := Some(
  Resolver.sftp("bone", "bone.twbbs.org.tw", "public_html/ivy")
)
