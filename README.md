Lift Combobox
==============

This is a [Lift web framework][01] module that let you create a jQuery-based [select2][02] combox using Scala code.

Currently it only support single-value select box.

[01]: http://liftweb.net/
[02]: http://ivaynberg.github.com/select2/

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

Using lift-combobox module
==========================

ScalaDoc API
-----------------

Here is the full ScalaDoc API about this module.

Initial lift-combox
---------------------

You need initialize lift-combox module in `Boot.scala`, so that it includes necessary JavaScript and CSS file to your website.

This is quite easily, just call `net.liftmodules.combobox.Combobox.init` in your `Boot` class.

    package bootstrap.liftweb
    import net.liftmodules.combobox.ComboBox
    
    class Boot 
    {
        def boot 
        {
            // ......

            ComboBox.init       // Initial lift-combobx module
        }
    }

Create combobox by Inheritance
-------------------------------

There are two ways to create combobox, you could create a class that extends from `net.liftmodules.combobox.ComboBox` class, or you could create combobox using `apply` method in ComboBox object. 

Both way are quite easy, so you could pick the method by your taste about how to code.

When you are using inheritance, you just create a object that extends from `net.liftmodules.combobox.ComboBox` class, and override the `onSearching(term: String): List[ComboItem]` method.

This method accept an arguement called `term`, which is what user input into the combobox. The return value is a `List[ComboItem]`, which will shows when the combobox is opened.

Here is the very basic example:

    import net.liftmodules.combobox.ComboItem
    import net.liftmodules.combobox.ComboBox

    val options = List("placeholder" -> "Choice the flavor your like")
    val comboBox = new ComboBox(
        default = None,     // The default value of this combo box.
        allowCreate = true, // Is user allowed to create item that does not exist?
        jsonOptions = options // The options of select2 combobox.
    ) {

        // This is where you build your combox suggestion
        override def onSearching(term: String): List[ComboItem] = {
            val flavor = ComboItem("f1", "Valina") :: ComboItem("f2", "Fruit") ::
                         ComboItem("f3", "Banana") :: ComboItem("f4", "Apple") ::
                         ComboItem("f5", "Chocolate") :: Nil

            flavor.filter(_.text.contains(term))
        }
        
    }
