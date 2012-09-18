package net.liftmodules.combobox

import net.liftweb.common._

import net.liftweb.json._
import net.liftweb.json.JsonParser
import net.liftweb.json.Serialization

import net.liftweb.http.LiftRules
import net.liftweb.http.JsonResponse
import net.liftweb.http.js.JE.JsRaw
import net.liftweb.http.js.JE.Call
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.js.JE._

import net.liftweb.http.SHtml
import net.liftweb.http.S
import net.liftweb.http.S._

import net.liftweb.util.Helpers

import scala.xml.NodeSeq

case class ComboItem(id: String, text: String)

object ComboBox
{
    def apply(default: Option[ComboItem],
              searching: (String) => List[ComboItem],
              itemSelected: (Option[ComboItem]) => JsCmd, 
              jsonOptions: List[(String, String)]): ComboBox = {

        new ComboBox(default, false, jsonOptions) {
            override def onItemSelected(item: Option[ComboItem]) = { itemSelected(item) }
            override def onSearching(term: String) = { searching(term) }
        }
    }

    def apply(default: Option[ComboItem],
              searching: (String) => List[ComboItem],
              itemSelected: (Option[ComboItem]) => JsCmd, 
              itemAdded: (String) => JsCmd,
              jsonOptions: List[(String, String)]): ComboBox = {

        new ComboBox(default, true, jsonOptions) {
            override def onItemSelected(item: Option[ComboItem]) = { itemSelected(item) }
            override def onItemAdded(text: String) = { itemAdded(text) }
            override def onSearching(term: String) = { searching(term) }
        }
    }

    def apply(default: Option[ComboItem], 
              searching: (String) => List[ComboItem],
              itemSelected: (Option[ComboItem]) => JsCmd): ComboBox = {

        ComboBox.apply(default, searching, itemSelected, Nil)
    }

    def apply(searching: (String) => List[ComboItem],
              itemSelected: (Option[ComboItem]) => JsCmd): ComboBox = {

        ComboBox.apply(None, searching, itemSelected, Nil)
    }

    def apply(default: Option[ComboItem],
              searching: (String) => List[ComboItem],
              itemSelected: (Option[ComboItem]) => JsCmd, 
              itemAdded: (String) => JsCmd): ComboBox = {

        ComboBox.apply(default, searching, itemSelected, itemAdded, Nil)
    }

    def apply(searching: (String) => List[ComboItem],
              itemSelected: (Option[ComboItem]) => JsCmd, 
              itemAdded: (String) => JsCmd): ComboBox = {

        ComboBox.apply(None, searching, itemSelected, itemAdded, Nil)
    }

    /**
     * register the resources with lift (typically in boot)
     */
    def init() {
        import net.liftweb.http.ResourceServer
  
        ResourceServer.allow({
            case "combobox" :: _ => true
        })
    }
}

abstract class ComboBox(default: Option[ComboItem], allowCreate: Boolean, 
                        jsonOptions: List[(String, String)] = Nil)
{
    private implicit val formats = DefaultFormats
    private val NewItemPrefix = Helpers.nextFuncName

    def onItemSelected(item: Option[ComboItem]): JsCmd = { Noop }
    def onItemAdded(text: String): JsCmd = { Noop }
    def onSearching(term: String): List[ComboItem]

    val comboBoxID = Helpers.nextFuncName
    val clear: JsCmd = JsRaw("""$("#%s").select2("val", "")""" format(comboBoxID))

    private def onItemSelected_*(value: String): JsCmd = {

        if (value == "undefined") {
            onItemSelected(None)
        } else {
            val item = JsonParser.parse(value).extract[ComboItem]

            item.id match {
                case x if x.startsWith(NewItemPrefix) => onItemAdded(item.text)
                case _ => onItemSelected(Some(item))
            }
        }
    }

    private val ajaxURL: String = {

        val ajaxFunc = (funcName: String) => {

            val term = S.param("term").getOrElse("")
            val itemList: List[ComboItem] = onSearching(term)
            val jsonOutput = Serialization.write(itemList)(DefaultFormats)

            JsonResponse(JsRaw(jsonOutput))
        }

        fmapFunc(SFuncHolder(ajaxFunc)){ funcName =>
           encodeURL(S.contextPath + "/" + LiftRules.ajaxPath+"?"+funcName+"=foo")
        }
    }

    private val select2JS = {
        val options = ("width" -> "200px") :: jsonOptions
        val jsOptions = JsRaw(options.map(t => "'%s': '%s'" format(t._1, t._2)).mkString(","))
        val defaultValue = default match {
            case None => "[]"
            case Some(item) => """{'id': '%s', 'text': '%s'}""" format(item.id, item.text)
        }

        val ajaxJS = """{
            url: "%s",
            dataType: 'json',
            data: function (term, page) {
                return { 'term': term }
            },
            results: function (data, page) {
                return {results: data};
            }
        }""".format(ajaxURL)

        if (allowCreate) {
            """
                $("#%s").select2({
                    %s,
                    ajax: %s,
                    initSelection: function(element, callback) {
                        var data = %s;
                        callback(data);
                    },
                    createSearchChoice: createNewItem
                });
            """.format(comboBoxID, jsOptions.toJsCmd, ajaxJS, defaultValue)
        } else {
            """
                $("#%s").select2({
                    %s,
                    ajax: %s,
                    initSelection: function(element, callback) {
                        var data = %s;
                        callback(data);
                    }
                });
            """.format(comboBoxID, jsOptions.toJsCmd, ajaxJS, defaultValue)
        }

    }

    def comboBox: NodeSeq = {

        val onSelectJS = SHtml.ajaxCall(Call("getValue"), onItemSelected_* _)._2.toJsCmd
        val onChangeJS = """
            $("#%s").on("change", function(e) { 
                %s 
            });
        """.format(comboBoxID, onSelectJS)

        val getValueJS = """
            function getValue() {
                var data = $('#%s').select2("data");

                if (data) {
                    return '{"id": "' + data.id + '", "text": "' + data.text + '" }';
                } else {
                    return 'undefined';
                }
            }
        """.format(comboBoxID)

        val createSearchChoiceJS = """
            function createNewItem (term, data) { 
                if ($(data).filter(function() { return this.text.localeCompare(term)===0; })
                           .length===0) {
                    return {"id": "%s" + term, "text": term};
                } 
            }
        """.format(NewItemPrefix)

        val onLoad = OnLoad(
            JsRaw(
                onChangeJS ++
                getValueJS ++
                createSearchChoiceJS ++
                select2JS
            )
        )

        <span>
          <head>
            <link rel="stylesheet" href={"/" + LiftRules.resourceServerPath + "/combobox/select2.css"} type="text/css" media="all" />
            <script type="text/javascript" src={"/" + LiftRules.resourceServerPath + "/combobox/select2.js"}></script>
            {Script(onLoad)}
          </head>
          <input type="hidden" id={comboBoxID} value={default.map(_.id).getOrElse("")}/>
        </span>
    }
}
