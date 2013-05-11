Lift Combobox
==============

Table of Contents
------------------

  - [Introduction](#introduction)
    - [ScalaDoc API](#scaladoc-api)
  - [Install using SBT](#install-using-sbt)
    - [Prerequisite](#prerequisite)
    - [Installation](#installation)
  - [Initialize Combobox Module in Lift](#initialize-combobox-module-in-lift)
  - [Single-Value ComboBox](#single-value-combobox)
    - [OO-Style](#oo-style)
    - [Functional-style](#functional-style)
  - [Multiple-Value ComboBox (Tagging)](#multiple-value-combobox-tagging)
    - [OO-style](#oo-style-1)
    - [Functional-style](#functional-style-1)

Introduction
-----------------

This is a [Lift web framework][01] module that let you create a jQuery-based [select2][02] combox using Scala code.

Lift Combobox support both signle-value drop-down style select field, or multiple-value field like tagging system you found on most Web 2.0 application.


### ScalaDoc API ###

Here is the full [ScalaDoc API][03] about this module.

[01]: http://liftweb.net/
[02]: http://ivaynberg.github.com/select2/
[03]: http://brianhsu.github.com/lift-combobox/api/

Install using SBT
------------------

### Prerequisite ####

  - SBT
  - Scala 2.10.0 or above
  - Lift 2.5-RC2 or above

### Installation ###

Lift Combobox module is published as JAR file, you could using the following setting in your SBT `build.sbt` file, it will fetch / update the JAR files you need when you build your project.


```scala
scalaVersion := "2.10.0"

resolvers += "bone" at "http://bone.twbbs.org.tw/ivy"

libraryDependencies += "net.liftmodules" %% "combobox" % "2.5-RC2-0.5"

```

Initialize Combobox Module in Lift
-----------------------------------

You need initialize lift-combox module in `Boot.scala`, so that it includes necessary JavaScript and CSS file to your website.

This is quite easily, just call `net.liftmodules.combobox.Combobox.init` in your `Boot` class.

```scala

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

```

Single-Value ComboBox
-----------------------

Currently Lift ComboBox module support both OO-style and functional-style to create single-value drop-down combobox. 

Both of them are easy to use, you could choose the method according to your taste about how to code.


### OO-Style ###

When you prefer OO style to coding, you just create a object that extends from `net.liftmodules.combobox.ComboBox` class, and override the following method:

```scala
onSearching(term: String): List[ComboItem]
```

This method accept an arguement called `term`, which is what user input into the combobox. The return value is a `List[ComboItem]`, which will shows when the combobox is opened.

You could also override the following method to to provide the behavior when user selected an item / canceled the selection or added new item directly, the returned JsCmd will be executed in client's browser.

```scala
onItemSelected(selected: Option[ComboItem]): JsCmd
onItemAdded(text: String): JsCmd
```

The following is the working example of a combobox that shows five ice-cream flavor in a drop-down menu, while user could enter custom text to add new item to combobox.

```scala
import net.liftweb.http.js.JE.Str
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds.Alert

import net.liftmodules.combobox.ComboItem
import net.liftmodules.combobox.ComboBox

// The options passed to select2, please consult the select2's document to
// know what options you could set.
val options = ("placeholder" -> Str("Choice the flavor your like")) :: Nil

val comboBox = new ComboBox(
  default = None,       // The default value of this combo box.
  allowCreate = true,   // Is user allowed to create item that does not exist?
  jsonOptions = options // The options of select2 combobox.
) {

  // This is where you build your combox suggestion
  override def onSearching(term: String): List[ComboItem] = {
    val flavor = List(
      ComboItem("f1", "Valina"), ComboItem("f2", "Fruit"),
      ComboItem("f3", "Banana"), ComboItem("f4", "Apple"),
      ComboItem("f5", "Chocolate")
    )

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
```

After you have created ComboBox object, just binding `comboBox` to where you want the combobox to display using Lift's CSS binding feature in your snippet.

```scala
"class=flavorInput" #> comboBox.comboBox
```

Now you should able to see an combo box when you browse the page.

### Functional-style ###

If you prefer functional style and high-order function, there is an factory method in `ComboBox` object for you.

There are two groups of factory function, one for ComboBox that allowed user add new item on the fly, and the one that does not allowd.

The following is the factory method that allows user to added new item in combobox.

```scala
// Using this group of functions when you want user is allowed to added new item.
def apply(searching: (String) ⇒ List[ComboItem], itemSelected: (Option[ComboItem]) ⇒ JsCmd, itemAdded: (String) ⇒ JsCmd): ComboBox
def apply(default: Option[ComboItem], searching: (String) ⇒ List[ComboItem], itemSelected: (Option[ComboItem]) ⇒ JsCmd, itemAdded: (String) ⇒ JsCmd): ComboBox
def apply(default: Option[ComboItem], searching: (String) ⇒ List[ComboItem], itemSelected: (Option[ComboItem]) ⇒ JsCmd, itemAdded: (String) ⇒ JsCmd, jsonOptions: List[(String, JsExp)]): ComboBox
```

The following is the factor method that generate read-only combobox.

```scala
// Using this group of functions when you want user is not allowed to added new item.
def apply(searching: (String) ⇒ List[ComboItem], itemSelected: (Option[ComboItem]) ⇒ JsCmd): ComboBox
def apply(default: Option[ComboItem], searching: (String) ⇒ List[ComboItem], itemSelected: (Option[ComboItem]) ⇒ JsCmd): ComboBox
def apply(default: Option[ComboItem], searching: (String) ⇒ List[ComboItem], itemSelected: (Option[ComboItem]) ⇒ JsCmd, jsonOptions: List[(String, JsExp)]): ComboBox
```

The usage is pretty much the same with using OO-style.

```scala
import net.liftweb.http.js.JE.Str
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds.Alert

import net.liftmodules.combobox.ComboItem
import net.liftmodules.combobox.ComboBox

val options = ("placeholder" -> Str("Choice the flavor your like")) :: Nil

// This is where you build your combox suggestion
def onSearching(term: String): List[ComboItem] = {
  val flavor = List(
    ComboItem("f1", "Valina"), ComboItem("f2", "Fruit"),
    ComboItem("f3", "Banana"), ComboItem("f4", "Apple"),
    ComboItem("f5", "Chocolate")
  )

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
```

And binding `comboBox.comboBox` to template in your snippet.

```scala
"class=flavorInput" #> comboBox.comboBox
```

Multiple-Value ComboBox (Tagging)
----------------------------------

Lift combobox module also support for multiple-value combobox, which is tagging system you will find in most Web 2.0 website.

There are also OO-style version and functional-style version to create mutlitple-value combobox.

### OO-style ###

To create multiple-value combobox, you also create a object extened from `ComboBox`, but will the following extra steps:

  1. Add `"multiple" -> JsTrue` to `jsonOptions` in constructor.
  2. Override the `def onMultiItemSelected(items: List[ComboItem]): JsCmd` method.

The following is code example that create a multiple-value combobox:


```scala
import net.liftweb.http.js.JE.Str
import net.liftweb.http.js.JE.JsTrue
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds.Alert

import net.liftmodules.combobox.ComboItem
import net.liftmodules.combobox.ComboBox

val options = List(
  "placeholder" -> Str("Choice the flavor your like"),
  "multiple" -> JsTrue  // We want to create multiple-value combobox.
)

val comboBox = new ComboBox(
  default = None,       // The default value of this combo box.
  jsonOptions = options // The options of select2 combobox.
) {

  // This is where you build your combox suggestion
  override def onSearching(term: String): List[ComboItem] = {
    val flavor = List(
      ComboItem("f1", "Valina"), ComboItem("f2", "Fruit"),
      ComboItem("f3", "Banana"), ComboItem("f4", "Apple"),
      ComboItem("f5", "Chocolate")
    )

    flavor.filter(_.text.contains(term))
  }

  override def onMultiItemSelected(items: List[ComboItem]): JsCmd = {
    
    // Do the server-side work.
    out.println("selected:" + items) 

    Alert("selected:" + items)
  }

}
```

### Functional-style ###

If you want create multiple-value combobox with functional-style, you could use the following factory method in `ComboBox`:

```scala
def apply(
 searching: (String) ⇒ List[ComboItem], 
 itemsSelected: (List[ComboItem]) ⇒ JsCmd, 
 allowCreate: Boolean, 
 jsonOptions: List[(String, JsExp)]
): ComboBox 
```

The following is a working example equivalent to the OO-style code:

```scala
import net.liftweb.http.js.JE.Str
import net.liftweb.http.js.JE.JsTrue
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds.Alert

import net.liftmodules.combobox.ComboItem
import net.liftmodules.combobox.ComboBox

val options = List(
  "placeholder" -> Str("Choice the flavor your like"),
  "multiple" -> JsTrue
)

// This is where you build your combox suggestion
def onSearching(term: String): List[ComboItem] = {
  val flavor = List(
    ComboItem("f1", "Valina"), ComboItem("f2", "Fruit"),
    ComboItem("f3", "Banana"), ComboItem("f4", "Apple"),
    ComboItem("f5", "Chocolate")
  )

  flavor.filter(_.text.contains(term))
}

// What you want to do when user selected or cancel an item.
//
// If user cancel selection by the X button in the combbox, 
// selected will be None, otherwise it will be Some[ComboItem].
def onItemsSelected(itmes: List[ComboItem]): JsCmd = {
  println("selected:" + items)
  Alert("You selected:" + items)
}

val comboBox = ComboBox(onSearching _, onItemsSelected _, options)

```

