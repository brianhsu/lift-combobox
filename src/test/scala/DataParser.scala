package net.liftmodules.combobox

import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers
import net.liftweb.util.StringHelpers

class DataParserSpec extends FunSpec with ShouldMatchers {

  describe("DataParser") {
    
    val newItemPrefix = StringHelpers.randomString(10)
    val dataParser = new DataParser(newItemPrefix)

    it ("should parse JArray to Left[List[ComboItem]]")  {
      val jsonData = """
        [
          {"id": "3436494", "text": "Item1"},
          {"id": "testID", "text": "ItemABCD"},
          {"id": "3436494", "text": "Item1"}
        ]
      """

      val selected = dataParser.parseSelected(jsonData)

      selected.isLeft should be === true
      selected.left.get should be === List(
        ComboItem("3436494", "Item1"), 
        ComboItem("testID", "ItemABCD"),
        ComboItem("3436494", "Item1")
      )

    }

    it ("should parse empty JArray to Left[Nil]")  {

      val jsonData = """[]"""
      val selected = dataParser.parseSelected(jsonData)

      selected.isLeft should be === true
      selected.left.get should be === Nil
    }

    it ("should parse null to Right[(None, false)]") {
      val jsonData = """null"""
      val selected = dataParser.parseSelected(jsonData)

      selected.isRight should be === true
      selected.right.get should be === None
    }

    it ("should parse new item object as Right(Some[ComboItem], true)") {
      val newItemID = newItemPrefix + "ItemID"
      val jsonData = """{"id": "%s", "text": "NewItem"}""".format(newItemID)
      val selected = dataParser.parseSelected(jsonData)

      selected.isRight should be === true
      selected.right.get should be === Some(ComboItem(newItemID, "NewItem"), true)
    }

    it ("should parse old item object as Right(Some[ComboItem], false)") {
      val jsonData = """{"id": "4281091", "text": "OldItem"}"""
      val selected = dataParser.parseSelected(jsonData)

      selected.isRight should be === true
      selected.right.get should be === Some(ComboItem("4281091", "OldItem"), false)
    }

  }
}
