/**************
 * Exceptions.scala
 *
 * (c) 2012 Jieren Chen
 * jieren.chen@gmail.com
 * 
 **************/

package proxontweet.utils

case class NoMatchException(message : String) extends Exception
case class NonMatchingKeyException(message : String) extends Exception
case class NonMatchingTypeException(message : String) extends Exception
case class ParseException(message : String) extends Exception
case class SerializationException(message : String) extends Exception
case class InvalidQueryDefinitionException(message : String) extends Exception
case class TwitterResponseException(message : String) extends Exception
case class EmptyQueryStringException(message :String) extends Exception
