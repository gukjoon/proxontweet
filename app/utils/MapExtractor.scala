/**************
 * MapExtractor.scala
 *
 * (c) 2012 Jieren Chen
 * jieren.chen@gmail.com
 * 
 **************/

package proxontweet.utils

import java.lang.reflect.{Field,Type,ParameterizedType}
import scala.reflect.Manifest
import net.liftweb.json.JsonAST._

object MapExtractor {
  
  /**************
   * PUBLIC API
   **************/
  def extract[CaseClass](jObj : JValue)(implicit mf : Manifest[CaseClass]) :  Any = {
    // Given an arbitary JSON value (net.liftweb.json.JValue), return a nested Map that matches the given case class
    jObj match {
      case JString(x) => extractPrimitive[CaseClass](x)
      case JInt(x) => extractPrimitive[CaseClass](x)
      case JBool(x) => extractPrimitive[CaseClass](x)
      case JDouble(x) => extractPrimitive[CaseClass](x)
      case JNull => extractPrimitive[CaseClass](null)
      case JArray(x) => 
	val fieldSubTypeManifest : Manifest[_] = getSubManifestFromGenericManifestList(mf.typeArguments)
	extractList(x)(fieldSubTypeManifest)
      case x : JObject => extractObject[CaseClass](x)
      case JNothing => throw new NonMatchingKeyException("No match on key") //JNothing is returned by the \ operator when there is no match
      case _ => throw new NoMatchException("No match. Case fall-through in extract")
    }
  }
  
  /**************
   * RECURSIVE CALLS
   **************/
  private def extractObject[CaseClass](jObj: JObject) (implicit mf : Manifest[CaseClass]) : Map[String,Any] = {
    val fields = getFieldsFromClass[CaseClass](mf)
    fields.toList map {f => 
		       val key : String = f._1
		       val valType : Type = f._2
		       val valTypeManifest = getManifestFromGenericType(valType)
		       val matched : JValue = jObj \ key
		       (key, extract(matched)(valTypeManifest))
		     } toMap
  }
  
  private def extractList[CaseClass](jList : List[JValue]) (implicit mf : Manifest[CaseClass]) : List[Any] = {
    val extractedList = jList map { f => extract[CaseClass](f) }
    extractedList
  }
  private def extractPrimitive[CaseClass](jVal : Any) (implicit mf : Manifest[CaseClass]) : Any = {
    if (jVal == null &&
	mf.erasure == manifest[scala.runtime.Null$].erasure) {
      //null value matches
      jVal
    }
    else if (jVal != null &&
	     mf.erasure != manifest[scala.runtime.Null$].erasure &&
	     jVal.isInstanceOf[CaseClass]) {
      //non-null value matches
      jVal
    }
    else {
      throw new NonMatchingTypeException("No match on type")
    }
  }
  
  /**************
   * FIELD EXTRACTION
   **************/
  private def getFieldsFromClass[CaseClass](implicit mf : Manifest[CaseClass]) : Map[String,Type] = {
    // Given a case class, get a mapping of field names to field types
    val classFields : List[Field] = mf.erasure.getDeclaredFields.toList
    classFields map { s =>
		       s.setAccessible(true) //otherwise is empty
		       (s.getName,extractTypeFromField(s))
		   } toMap
  }
  
  private def extractTypeFromField(field : Field) : Type = {
    field.getGenericType
  }
  
  private def getSubManifestFromGenericManifestList(subTypes : List[Manifest[_]]) : Manifest[_] = {
    if (subTypes.length == 1) {
      return subTypes(0)
    }
    else {
      throw new InvalidQueryDefinitionException("Invalid collection type. Needs to be parameterized")    
    }
  }
  
  private def getManifestFromGenericType(genericType : Type) : Manifest[_] = {
    genericType match {
      case t : Class[_] => Manifest.classType(t)
      case t : ParameterizedType => Manifest.classType(t.getRawType().asInstanceOf[Class[_]], getManifestFromGenericType(t.getActualTypeArguments()(0)))
      case t : Type => Manifest.classType(t.asInstanceOf[Class[_]])
    }
  }
}
