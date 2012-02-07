/**************
 * controllers.scala
 *
 * (c) 2012 Jieren Chen
 * jieren.chen@gmail.com
 * 
 **************/

package controllers

import play._
import play.mvc._
import play.libs.WS
import net.liftweb.json._
import proxontweet.utils._

object SearchApplication extends Controller {
  implicit val formats = net.liftweb.json.DefaultFormats // needed for json.parse
  
  /**************
   * QUERY CASE CLASSES
   **************/
  case class StatusSearchResult(from_user : String, text : String)
  case class StatusSearchResultsResponse(completed_in : Double, query: String, results : List[StatusSearchResult])
    
  /**************
   * PUBLIC API
   **************/
  def searchStatus (q : String) = {
    try {
      if (q.length == 0) {
	throw EmptyQueryStringException("Empty query string")
      }
      val statusSearchResultsResponse = getStatusSearchResponse(q)
      val statusSearchResultsMap = extractResultsToMap[StatusSearchResultsResponse](statusSearchResultsResponse)
      Json(JSONSerializer.serialize(statusSearchResultsMap))
    }
    catch {
      case EmptyQueryStringException(message) => NotFound(message) //Play does not allow a message for BadRequest (400), which is a more applicable error code than NotFound(404). Showing the message is more important.
      case NoMatchException(message) => Error(message)
      case NonMatchingKeyException(message) => Error(message)
      case NonMatchingTypeException(message) => Error(message)
      case InvalidQueryDefinitionException(message) => Error(message)
      case ParseException(message) => Error(message)
      case TwitterResponseException(message) => Error(message)
    }
  }
  
  /**************
   * HELPER FUNCTIONS
   **************/
  private def getStatusSearchResponse (q : String) : String = {
    var twitterUrl = WS.url(Play.configuration.get("search.status.url").asInstanceOf[String] + q)
    try {
      twitterUrl.get.getString
    }
    catch {
      case _ => throw new TwitterResponseException("Something went wrong with Twitter")
    }
  }
  
  private def extractResultsToMap[CaseClass](response : String)(implicit mf : Manifest[CaseClass]) : Any = {
    val parsedMap = net.liftweb.json.parse(response)
    if (parsedMap == JNothing) {
      throw new ParseException("Unable to parse " + response)
    }
    val extractedMap = MapExtractor.extract[CaseClass](parsedMap)(mf)
    extractedMap
  }
}
