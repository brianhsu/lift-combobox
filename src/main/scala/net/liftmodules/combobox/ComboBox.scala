package net.liftmodules.combobox

import net.liftweb.common._

import net.liftweb.json._
import net.liftweb.json.JsonParser
import net.liftweb.json.Serialization

import net.liftweb.http.LiftRules
import net.liftweb.http.JsonResponse
import net.liftweb.http.LiftResponse

import net.liftweb.http.js.JE.JsRaw
import net.liftweb.http.js.JE.Call
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.js.JE._
import net.liftweb.http.js.JsExp

import net.liftweb.http.SHtml
import net.liftweb.http.S
import net.liftweb.http.S._

import net.liftweb.util.Helpers

import scala.xml.NodeSeq

/**
 *  CombBox 
 *
 *  You could use factory methods in this object to create ComboBox widget.
 */
object ComboBox
{
  /**
   *  Create ComboBox widget that does not allowed user create new items.
   *
   *  @param  default         The default value of combobox when page is loaded.
   *  @param  searching       The builiding function of suggestion list, 
   *                          the String argument is what user input into the combobox.
   *  @param  itemSelected    What to do if user selected an item in combobox, 
   *                          the returned JsCmd will execute on client side.
   *  @param  jsonOptions     The options passed to select2.
   */
  def apply(default: List[ComboItem],
            searching: (String) => List[ComboItem],
            itemSelected: (Option[ComboItem]) => JsCmd, 
            jsonOptions: List[(String, JsExp)]): ComboBox = {
  
    new ComboBox(default, false, jsonOptions) {
      override def onItemSelected(item: Option[ComboItem]) = { itemSelected(item) }
      override def onSearching(term: String) = { searching(term) }
    }
  }
  
  /**
   *  Create ComboBox widget that does not allowed user create new items.
   *
   *  @param  default         The default value of combobox when page is loaded.
   *  @param  searching       The builiding function of suggestion list, 
   *                          the String argument is what user input into the combobox.
   *  @param  itemSelected    What to do if user selected an item in combobox, 
   *                          the returned JsCmd will execute on client side.
   */
  def apply(default: List[ComboItem], 
            searching: (String) => List[ComboItem],
            itemSelected: (Option[ComboItem]) => JsCmd): ComboBox = {
  
    ComboBox.apply(default, searching, itemSelected, Nil)
  }
  
  /**
   *  Create ComboBox widget that does not allowed user create new items.
   *
   *  @param  searching       The builiding function of suggestion list, 
   *                          the String argument is what user input into the combobox.
   *  @param  itemSelected    What to do if user selected an item in combobox, 
   *                          the returned JsCmd will execute on client side.
   */
  def apply(searching: (String) => List[ComboItem],
            itemSelected: (Option[ComboItem]) => JsCmd): ComboBox = {
  
    ComboBox.apply(Nil, searching, itemSelected, Nil)
  }
  
  /**
   *  Create ComboBox widget that allowed user create new items.
   *
   *  @param  default         The default value of combobox when page is loaded.
   *  @param  searching       The builiding function of suggestion list, 
   *                          the String argument is what user input into the combobox.
   *  @param  itemSelected    What to do if user selected an item in combobox, 
   *                          the returned JsCmd will execute on client side.
   *  @param  itemAdded       What to do if user added an item into combobox,
   *                          the returned JsCmd will execute on client side.
   *  @param  jsonOptions     The options passed to select2.
   */
  def apply(default: List[ComboItem],
            searching: (String) => List[ComboItem],
            itemSelected: (Option[ComboItem]) => JsCmd, 
            itemAdded: (String) => JsCmd,
            jsonOptions: List[(String, JsExp)]): ComboBox = {
  
    new ComboBox(default, true, jsonOptions) {
      override def onItemSelected(item: Option[ComboItem]) = { itemSelected(item) }
      override def onItemAdded(text: String) = { itemAdded(text) }
      override def onSearching(term: String) = { searching(term) }
    }
  }
  
  
  /**
   *  Create ComboBox widget that allowed user create new items.
   *
   *  @param  default         The default value of combobox when page is loaded.
   *  @param  searching       The builiding function of suggestion list, 
   *                          the String argument is what user input into the combobox.
   *  @param  itemSelected    What to do if user selected an item in combobox, 
   *                          the returned JsCmd will execute on client side.
   *  @param  itemAdded       What to do if user added an item into combobox,
   *                          the returned JsCmd will execute on client side.
   */
  def apply(default: List[ComboItem],
            searching: (String) => List[ComboItem],
            itemSelected: (Option[ComboItem]) => JsCmd, 
            itemAdded: (String) => JsCmd): ComboBox = {
  
    ComboBox.apply(default, searching, itemSelected, itemAdded, Nil)
  }
  
  /**
   *  Create ComboBox widget that allowed user create new items.
   *
   *  @param  searching       The builiding function of suggestion list, 
   *                          the String argument is what user input into the combobox.
   *  @param  itemSelected    What to do if user selected an item in combobox, 
   *                          the returned JsCmd will execute on client side.
   *  @param  itemAdded       What to do if user added an item into combobox,
   *                          the returned JsCmd will execute on client side.
   */
  def apply(searching: (String) => List[ComboItem],
            itemSelected: (Option[ComboItem]) => JsCmd, 
            itemAdded: (String) => JsCmd): ComboBox = {
  
    ComboBox.apply(Nil, searching, itemSelected, itemAdded, Nil)
  }
  
  /**
   *  Create ComboBox widget that allowed multiple values.
   *
   *  @param  searching       The builiding function of suggestion list, 
   *                          the String argument is what user input into the combobox.
   *  @param  itemsSelected   What to do if user selected / unselect  item in combobox, 
   *                          the returned JsCmd will execute on client side.
   */
  def apply(default: List[ComboItem],
            searching: (String) => List[ComboItem],
            itemsSelected: (List[ComboItem]) => JsCmd, 
            allowCreate: Boolean,
            jsonOptions: List[(String, JsExp)]): ComboBox = {

    new ComboBox(default, allowCreate, jsonOptions) {
      override def onSearching(term: String) = { searching(term) }
      override def onMultiItemSelected(items: List[ComboItem]): JsCmd = { 
        itemsSelected(items) 
      }
    }
  }


  /**
   *  Register the resources with lift.
   *
   *  You should call this method in your `Boot` class to initialize ComboBox module.
   */
  def init() {
    import net.liftweb.http.ResourceServer
  
    ResourceServer.allow({
      case "combobox" :: _ => true
    })
  }
}


/**
 *  The ComboBox Widget
 *
 *  This widget use <a href="http://ivaynberg.github.com/select2/">select2</a> 
 *  to generate the ComboBox.
 *
 *  You could pass options to select2 by using jsonOptions parameter in the
 *  constructor.
 *
 *  When using this class, you should override at least onSearching, so the 
 *  combobox knows what should be displayed when user opened this comboBox.
 *
 *  Please see the <a href="https://github.com/brianhsu/lift-combobox">README file</a> 
 *  to know how to acutually use this class to createa combobox in your 
 *  Lift web project.
 *
 *  @param  default         The default item in the combobox.
 *  @param  allowCreate     Is user allowed to enter item that does not on the sugeestion list.
 *  @param  jsonOptions     The options should pass to select2.
 */
abstract class ComboBox(default: List[ComboItem], allowCreate: Boolean, 
                        jsonOptions: List[(String, JsExp)] = Nil) extends DropDownMenu
{
  private val NewItemPrefix = Helpers.nextFuncName
  private val dataParser = new DataParser(NewItemPrefix)

  /**
   *  The method that build suggestion list.
   *  
   *  @param  term    The text user input into the combobox.
   *  @return         The combobox suggestion items.
   */
  def onSearching(term: String): List[ComboItem]

  /**
   *  What we should do when user selected / canceled an item.
   *
   *  @param  item    If user selected an item, it will be Some[ComboItem].
   *                  if user canceled the current item, it will be None.
   *  @return         What JsCmd should execute on client side.
   */
  def onItemSelected(item: Option[ComboItem]): JsCmd = Noop

  /**
   *  What we should do when user selected / canceled an item on multiselect.
   *
   *  @param  item    If user selected an item, it will be Some[ComboItem].
   *                  if user canceled the current item, it will be None.
   *  @return         What JsCmd should execute on client side.
   */
  def onMultiItemSelected(items: List[ComboItem]): JsCmd = Noop

  /**
   *  What we should do when user added an item.
   *
   *  @param  text    The text user entered into combo box.
   *  @return         What JsCmd should execute on client side.
   */
  def onItemAdded(text: String): JsCmd = Noop

  /**
   *  The id of HTML hidden input box associate with select2 combobox.
   */
  val comboBoxID = Helpers.nextFuncName

  /**
   *  The JsCmd that could clear current selection in the combobox.
   */
  val clear: JsCmd = JsRaw(raw"""$$("#${comboBoxID}").select2("val", "")""")

  /**
   *  This will be called when user selected or inserted an item in the combobox,
   *  and delegated to the corresponding callback function.
   *
   *  @param  value   The selected item.
   *  @return         The JavaScript should be exectued on client side.
   */
  private def onItemSelected_*(value: String): JsCmd = {
    dataParser.parseSelected(value) match {
      case Left(items) => onMultiItemSelected(items)
      case Right(None) => onItemSelected(None)
      case Right(Some((item, true))) => onItemAdded(item.text)
      case Right(Some((item, false))) => onItemSelected(Some(item))
    }
  }


  /**
   *  Register the onSearching method as an Lift's ajax function that
   *  could access from URL.
   *
   *  @return   The AJAX callback URL.
   */
  override protected def initAjaxURL() = {

    val ajaxFunc = (funcName: String) => searchAjax(S.param("term").getOrElse(""))

    fmapFunc(SFuncHolder(ajaxFunc)) { funcName =>
      encodeURL(S.hostAndPath+ "/" + LiftRules.liftPath+"/ajax/"+S.renderVersion+"?"+funcName)
    }
  }

  /**
   *  The search function AJAX callback URL.
   */
  private val ajaxURL: String = initAjaxURL

  /**
   *  Convert options to JavaScript
   *
   *  @param    options   The options of ComboBox
   *  @return             The corresponding options in JavaScript format.
   */
  private def convertOptionsToJS(options: List[(String, JsExp)]) = {
    val jsonOptions = options.map { case(k, v) => s"'$k': ${v.toJsCmd}" }
    jsonOptions.mkString(",")
  }

  /**
   *  The JavaScript code that initial the combobox.
   */
  private val select2JS = {

    val options: List[(String, JsExp)] = ("width" -> Str("200px")) :: jsonOptions
    val jsOptions = convertOptionsToJS(options)

    val defaultValue = default match {
      case Nil   => "[]"
      case items => items.map { item => 
        s"""{'id': '${item.id}', 'text': '${item.text}'}"""
      }.mkString("[", ",", "]")
    }

    val ajaxJS = raw"""{
      url: '$ajaxURL',
      dataType: 'json',
      data: function (term, page) {
        return { 'term': term }
      },
      results: function (data, page) {
        return {results: data};
      }
    }"""

    val allowCreateJS = raw"""
      $$("#$comboBoxID").select2({
        $jsOptions,
        ajax: $ajaxJS,
        initSelection: function(element, callback) {
          var data = $defaultValue;
          callback(data);
        },
        createSearchChoice: createNewItem
      });
    """

    val notAllowCreateJS = raw"""
      $$("#$comboBoxID").select2({
        $jsOptions,
        ajax: $ajaxJS,
        initSelection: function(element, callback) {
          var data = $defaultValue;
          callback(data);
        }
      })
    """

    allowCreate match {
      case true => allowCreateJS
      case false => notAllowCreateJS
    }

  }

  /**
   *  The ComboBox HTML code that could bind to template.
   */
  def comboBox: NodeSeq = {

    val onSelectJS = SHtml.ajaxCall(Call("getJSON"), onItemSelected_* _).toJsCmd
    val jsFunctions = raw"""

      /**
       *  Convert user selection in select2 to JSON string.
       *
       *  @return   The item(s) user selected in select2 in JSON format.
       */
      function getJSON() {
        var jsonData = JSON.stringify($$('#${comboBoxID}').select2("data"));
        return jsonData;
      }

      /**
       *  The callback for user to create an new item.
       */
      function createNewItem (term, data) { 
        if ($$(data).filter(function() { return this.text.localeCompare(term)===0; })
                   .length===0) {
          return {"id": '${NewItemPrefix}' + term, "text": term};
        } 
      }

      $$("#${comboBoxID}").on("change", function(e) { 
        ${onSelectJS}
      });

    """

    val onLoad = OnLoad(
      JsRaw(
        jsFunctions ++
        select2JS
      )
    )

    <span>
      <head>
        <link rel="stylesheet" href={"/" + LiftRules.resourceServerPath + "/combobox/select2.css"} type="text/css" media="all" />
        <script type="text/javascript" src={"/" + LiftRules.resourceServerPath + "/combobox/select2.js"}></script>
        {Script(onLoad)}
      </head>
      <input type="hidden" id={comboBoxID} value={default.map(_.id).mkString(",")}/>
    </span>
  }
}
