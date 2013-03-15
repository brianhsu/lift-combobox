package net.liftmodules.combobox

import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers

import net.liftweb.json._
import net.liftweb.http.JsonResponse

class DropDownMenuSpec extends FunSpec with ShouldMatchers {

  implicit val formatter = DefaultFormats

  describe("DropDownMenu") {
      
    it ("should generate JSON menu correctly if there is matching items") {
      val dropDownMenu = new MockDropDownMenu
      val json = dropDownMenu.searchAjax("ext").asInstanceOf[JsonResponse].json.toJsCmd
      val items = JsonParser.parse(json).children.map(_.extract[ComboItem])

      items should be === List(ComboItem("id1", "text1"), ComboItem("id2", "text2"))
    }

    it ("should generate empty JSON menu correct if there is no matching items") {
      val dropDownMenu = new MockDropDownMenu
      val json = dropDownMenu.searchAjax("nothing").asInstanceOf[JsonResponse].json.toJsCmd
      val items = JsonParser.parse(json).children.map(_.extract[ComboItem])

      items should be === Nil
    }
  }
}
