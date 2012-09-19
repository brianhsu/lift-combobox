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

Here is the full [ScalaDoc API][03] about this module.

[03]: http://brianhsu.github.com/lift-combobox/scaladoc/#package

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

Create ComboBox by Inheritance
-------------------------------

There are two ways to create combobox, you could create a class that extends from `net.liftmodules.combobox.ComboBox` class, or you could create combobox using `apply` method in ComboBox object. 

Both way are quite easy, so you could pick the method by your taste about how to code.

When you are using inheritance, you just create a object that extends from `net.liftmodules.combobox.ComboBox` class, and override the `onSearching(term: String): List[ComboItem]` method.

This method accept an arguement called `term`, which is what user input into the combobox. The return value is a `List[ComboItem]`, which will shows when the combobox is opened.

Here is the very basic example:

    import net.liftweb.http.js.JE.Str
    import net.liftweb.http.js.JsCmd
    import net.liftweb.http.js.JsCmds.Alert

    import net.liftmodules.combobox.ComboItem
    import net.liftmodules.combobox.ComboBox

    // The options passed to select2, please consult the select2's document to
    // know what options you could set.
    val options = ("placeholder" -> Str("Choice the flavor your like")) :: Nil

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

        // What you want to do when user selected or cancel an item.
        //
        // If user cancel selection by the X button in the combbox, 
        // selected will be None, otherwise it will be Some[ComboItem].
        override def onItemSelected(selected: Option[ComboItem]): JsCmd = {
            println("selected:" + selected)

            // The returned JsCmd will be executed on client side.
            Alert("You selected:" + selected)
        }


        // What you want to do if user added an item that
        // does not exist when allowCreate = true.
        override def onItemAdded(text: String): JsCmd = {
            // save this item to database or anything you want to do
            println("user added " + text)

            // The returned JsCmd will be executed on client side.
            Alert("Saved " + text + " to database")
        }
    }

After you have created ComboBox object, just binding `comboBox` to where you want the combobox to display using Lift's CSS binding feature in your snippet.

    "class=flavorInput" #> comboBox.comboBox

Now you should able to see an combo box when you browse the page.

Create ComboBox by Functions
-------------------------------

If you prefer functional style and high-order function, there is an factory method in ComboBox object for you.

The following two groups of factory function, one for ComboBox that allowed user add new item on the fly, and the one that does not allowd.

    // Using this group of functions when you want user is allowed to added new item.
    def apply(searching: (String) ⇒ List[ComboItem], itemSelected: (Option[ComboItem]) ⇒ JsCmd, itemAdded: (String) ⇒ JsCmd): ComboBox
    def apply(default: Option[ComboItem], searching: (String) ⇒ List[ComboItem], itemSelected: (Option[ComboItem]) ⇒ JsCmd, itemAdded: (String) ⇒ JsCmd): ComboBox
    def apply(default: Option[ComboItem], searching: (String) ⇒ List[ComboItem], itemSelected: (Option[ComboItem]) ⇒ JsCmd, itemAdded: (String) ⇒ JsCmd, jsonOptions: List[(String, JsExp)]): ComboBox

    // Using this group of functions when you want user is not allowed to added new item.
    def apply(searching: (String) ⇒ List[ComboItem], itemSelected: (Option[ComboItem]) ⇒ JsCmd): ComboBox
    def apply(default: Option[ComboItem], searching: (String) ⇒ List[ComboItem], itemSelected: (Option[ComboItem]) ⇒ JsCmd): ComboBox
    def apply(default: Option[ComboItem], searching: (String) ⇒ List[ComboItem], itemSelected: (Option[ComboItem]) ⇒ JsCmd, jsonOptions: List[(String, JsExp)]): ComboBox

The usage is pretty much the same with using inheritance.

    import net.liftweb.http.js.JE.Str
    import net.liftweb.http.js.JsCmd
    import net.liftweb.http.js.JsCmds.Alert

    import net.liftmodules.combobox.ComboItem
    import net.liftmodules.combobox.ComboBox

    val options = ("placeholder" -> Str("Choice the flavor your like")) :: Nil

    // This is where you build your combox suggestion
    def onSearching(term: String): List[ComboItem] = {
        val flavor = ComboItem("f1", "Valina") :: ComboItem("f2", "Fruit") ::
                     ComboItem("f3", "Banana") :: ComboItem("f4", "Apple") ::
                     ComboItem("f5", "Chocolate") :: Nil

        flavor.filter(_.text.contains(term))
    }

    // What you want to do when user selected or cancel an item.
    //
    // If user cancel selection by the X button in the combbox, 
    // selected will be None, otherwise it will be Some[ComboItem].
    def onItemSelected(selected: Option[ComboItem]): JsCmd = {
        println("selected:" + selected)

        // The returned JsCmd will be executed on client side.
        Alert("You selected:" + selected)
    }

    // What you want to do if user added an item that
    // does not exist when allowCreate = true.
    def onItemAdded(text: String): JsCmd = {
        // save this item to database or anything you want to do
        println("user added " + text)

        // The returned JsCmd will be executed on client side.
        Alert("Saved " + text + " to database")
    }

    val comboBox = ComboBox(None, onSearching _, onItemSelected _, onItemAdded _, options)

And binding `comboBox.comboBox` to template in your snippet.

    "class=flavorInput" #> comboBox.comboBox

