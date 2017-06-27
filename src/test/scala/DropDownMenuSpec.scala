package net.liftmodules.combobox

import org.scalatest.{FunSpec, Matchers}
import net.liftweb.json._
import net.liftweb.http.JsonResponse

class DropDownMenuSpec extends FunSpec with Matchers {

  implicit val formatter = DefaultFormats

  describe("DropDownMenu") {
      
    it ("should generate JSON menu correctly if there is matching items") {
      val dropDownMenu = new MockDropDownMenu
      val json = dropDownMenu.searchAjax("ext").asInstanceOf[JsonResponse].json.toJsCmd
      val items = JsonParser.parse(json).children.map(_.extract[ComboItem])

      items shouldEqual List(ComboItem("id1", "text1"), ComboItem("id2", "text2"))
    }

    it ("should generate empty JSON menu correct if there is no matching items") {
      val dropDownMenu = new MockDropDownMenu
      val json = dropDownMenu.searchAjax("nothing").asInstanceOf[JsonResponse].json.toJsCmd
      val items = JsonParser.parse(json).children.map(_.extract[ComboItem])

      items shouldEqual Nil
    }
  }
}
