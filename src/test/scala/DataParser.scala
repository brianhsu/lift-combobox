package net.liftmodules.combobox

import org.scalatest.{FunSpec, Matchers}
import net.liftweb.util.StringHelpers

class DataParserSpec extends FunSpec with Matchers {

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

      selected.isLeft shouldEqual true
      selected.left.get shouldEqual List(
        ComboItem("3436494", "Item1"), 
        ComboItem("testID", "ItemABCD"),
        ComboItem("3436494", "Item1")
      )

    }

    it ("should parse empty JArray to Left[Nil]")  {

      val jsonData = """[]"""
      val selected = dataParser.parseSelected(jsonData)

      selected.isLeft shouldEqual true
      selected.left.get shouldEqual Nil
    }

    it ("should parse null to Right[(None, false)]") {
      val jsonData = """null"""
      val selected = dataParser.parseSelected(jsonData)

      selected.isRight shouldEqual true
      selected.right.get shouldEqual None
    }

    it ("should parse new item object as Right(Some[ComboItem], true)") {
      val newItemID = newItemPrefix + "ItemID"
      val jsonData = """{"id": "%s", "text": "NewItem"}""".format(newItemID)
      val selected = dataParser.parseSelected(jsonData)

      selected.isRight shouldEqual true
      selected.right.get shouldEqual Some(ComboItem(newItemID, "NewItem"), true)
    }

    it ("should parse old item object as Right(Some[ComboItem], false)") {
      val jsonData = """{"id": "4281091", "text": "OldItem"}"""
      val selected = dataParser.parseSelected(jsonData)

      selected.isRight shouldEqual true
      selected.right.get shouldEqual Some(ComboItem("4281091", "OldItem"), false)
    }

  }
}
