package net.liftmodules.combobox

import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers
import net.liftweb.util.StringHelpers

class ComboBoxSpec extends FunSpec with ShouldMatchers {

  class MockComboBox extends ComboBox(None, false) {
    override def initAjaxURL = "http://localhost/temp"
    override def onSearching(term: String): List[ComboItem] = {
      val items = ComboItem("id1", "text1") :: ComboItem("id2", "text2") :: Nil
      items.filter(_.text.contains(term))
    }
  }

  describe("ComboBox") {
    
    val comboBox = new MockComboBox

    it ("should parse JArray to Left[List[ComboItem]]")  {
      pending
    }

  }
}
