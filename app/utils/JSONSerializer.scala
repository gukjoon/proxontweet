/**************
 * JSONSerializer.scala
 *
 * (c) 2012 Jieren Chen
 * jieren.chen@gmail.com
 * 
 **************/

package proxontweet.utils

object JSONSerializer {

  /**************
   * PUBLIC API
   **************/
  def serialize(theObj : Any) : String = {
    // Given a nested map, returns a JSON string
    theObj match {
      case v : Map[String,Any] => serializeMap(v)
      case v : List[Any] => serializeList(v)
      case _ => serializePrimitive(theObj)
    }
  }

  /**************
   * RECURSIVE CALLS
   **************/  
  private def serializeMap(theMap : Map[String,Any]) : String = {
    val mapped = theMap.toList map { item => "\"" + item._1 + "\" : " + serialize(item._2) }
    "{" + mapped.reduceLeft(reducer) + "}"
  }
  
  private def serializeList(theList : List[Any]) : String = {
    val mapped = theList map { item => serialize(item) }
    "[" + mapped.reduceLeft(reducer)+ " ]"
  }
  
  private def serializePrimitive(thePrimitive : Any) : String = {
    // Given a non-Map, non-List object, tries to serialize as JSON primitive
    thePrimitive match {
      case v : String => "\"" + v + "\""
      case null => "null"
      case v : Number => v.toString
      case v : Boolean => v.toString
      case v : AnyRef => throw new SerializationException("Invalid primitive: " + thePrimitive)
      case _ => thePrimitive.toString //covers java primitives
    }
  }
  
  /**************
   * UTILITIES
   **************/
  private def reducer(a : String, b : String)  : String = {
    a + " , " + b
  }
}
