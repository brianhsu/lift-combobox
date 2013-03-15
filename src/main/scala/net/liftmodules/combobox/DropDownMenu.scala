package net.liftmodules.combobox

import net.liftweb.http.JsonResponse
import net.liftweb.http.LiftResponse

import net.liftweb.json.Serialization
import net.liftweb.json._
import net.liftweb.http.js.JE.JsRaw

/**
 *  This trait implements the function of filtering
 *  drop down menu of select2 when user click it.
 */
trait DropDownMenu {

  /**
   *  Implement this method to provide drop down menu.
   *
   *  When user enter text into select2's filter field,
   *  it will call this function, and returned the List[ComboItem]
   *  to select2 to show drop-down menu.
   *
   *  @param  term  The text user entered.
   *  @return       The item of drop-down menu.
   */
  def onSearching(term: String): List[ComboItem]

  /**
   *  Register Ajax callback URL to lift.
   *
   *  @return       The ajax callback URL.
   */
  protected def initAjaxURL(): String

  /**
   *  Search AJAX function.
   *
   *  This will call `onSearching` and convert the result to 
   *  JSON format, and provide it to select2.
   *
   *  @param  term    The text user entered.
   *  @return         The matching item as JSON response output.
   */
  protected[combobox] def searchAjax(term: String): LiftResponse = {
    val itemList: List[ComboItem] = onSearching(term)
    val jsonOutput = Serialization.write(itemList)(DefaultFormats)

    JsonResponse(JsRaw(jsonOutput))
  }
}

