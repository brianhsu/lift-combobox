Lift Combobox
==============

This is a lift module that let you create a jQuery-based select2 combox using Scala code.

Currently it only support single-value select box.

Build and Install using SBT
=============================

Because this module is in alpha status, not fully tested and feature complete. It may has many bugs or missing some important feature, so I didn't publish it to any public Maven repository.

But if you are instrested in this, you could easily build and install it to your local repository by using SBT.

Here is the command that pull the most recent version of this module from GitHub and install it into local Maven repository.

    $ git clone git://github.com/brianhsu/lift-combobox.git
    $ cd lift-combobox/
    $ sbt publish-local

Add lift-combox as Dependency in SBT project
=============================================

After build and publish this module to your local repository, you could using the following code in your project's `.sbt` file to include this module.

    libraryDependencies ++= Seq(
        "net.liftmodules" %% "combobox" % "2.5-SNAPSHOT-0.1"
    )

Using the module
==========================


