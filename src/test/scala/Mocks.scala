package net.liftmodules.combobox

class MockDropDownMenu extends DropDownMenu {
  override def initAjaxURL = "http://localhost/testAjax"
  override def onSearching(term: String): List[ComboItem] = {

    val items = List( 
      ComboItem("id1", "text1"), ComboItem("id2", "text2"),
      ComboItem("id3", "aaaa1"), ComboItem("id4", "bbbbb")
    )

    items.filter(_.text.contains(term))
  }
}

