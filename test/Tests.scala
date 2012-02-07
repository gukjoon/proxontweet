import play._
import play.libs.WS
import play.mvc._
import play.test._

import org.scalatest._
import org.scalatest.junit._
import org.scalatest.matchers._

import net.liftweb.json._
import proxontweet.utils._

case class JSONSimpleTestClass (a : String, b : Boolean, c : Int, d: Double, e:Null)

case class JSONListTestClass (a : String, c : List[Int])

case class JSONNestedChildTestClass (d : String)
case class JSONNestedParentTestClass (a : String, c : JSONNestedChildTestClass)

case class JSONNestedListChildTestClass (c : String)
case class JSONNestedListParentTestClass (a : String, b : List[JSONNestedListChildTestClass])

class JSONExtractTests extends UnitFunSuite {
  test("verify extractor simple") {
    val testJson = "{ \"a\" : \"a\", \"b\" : true, \"c\" : 1, \"d\" : 2.2, \"e\" : null}"
    val jsonSimpleTestMap = Map( "a" -> "a", "b" -> true, "c" -> 1, "d" -> 2.2, "e" -> null)
    val parsedMap = net.liftweb.json.parse(testJson)
    val extractedMap = MapExtractor.extract[JSONSimpleTestClass](parsedMap)
    assert(extractedMap == jsonSimpleTestMap)
  }
  
  test("verify extractor with lists") {
    val testJson = "{ \"a\" : \"b\", \"c\" : [1, 2, 3]}"
    val jsonListTestMap = Map( "a" -> "b", "c" -> List[Int](1,2,3))
    val parsedMap = net.liftweb.json.parse(testJson)
    val extractedMap = MapExtractor.extract[JSONListTestClass](parsedMap)
    assert(extractedMap == jsonListTestMap)
  }
  
  test("verify extractor nested") {
    val testJson = "{ \"a\" : \"b\", \"c\" : { \"d\" : \"e\" }}"
    val jsonNestedTestMap = Map( "a" -> "b", "c" -> Map( "d" -> "e"))
    val parsedMap = net.liftweb.json.parse(testJson)
    val extractedMap = MapExtractor.extract[JSONNestedParentTestClass](parsedMap)
    assert(extractedMap == jsonNestedTestMap)
  }

  test("verify extractor nested list") {
    val testJson = "{ \"a\" : \"a\", \"b\" : [{\"c\" : \"d\"} , { \"c\" : \"e\" }]}"
    val jsonNestedListTestMap = Map( "a" -> "a", "b" -> List( Map("c" -> "d"), Map( "c" -> "e")))
    val parsedMap = net.liftweb.json.parse(testJson)
    val extractedMap = MapExtractor.extract[JSONNestedListParentTestClass](parsedMap)
    assert(extractedMap == jsonNestedListTestMap)
  }
}

class JSONFailureTests extends UnitFunSuite {
  test("no match on key") {
    val testJson = "{ \"a\" : \"a\", \"b\" : true, \"c\" : 1, \"d\" : 2.2}"
    val parsedMap = net.liftweb.json.parse(testJson)
    intercept[NonMatchingKeyException] {
      MapExtractor.extract[JSONSimpleTestClass](parsedMap)
    }
  }
  
  test("no match on type") {
    val testJson = "{ \"a\" : \"a\", \"b\" : true, \"c\" : 1, \"d\" : 2.2, \"e\" : 100}"
    val parsedMap = net.liftweb.json.parse(testJson)
    intercept[NonMatchingTypeException] {
      MapExtractor.extract[JSONSimpleTestClass](parsedMap)
    }    
  }
  
  test("verify bad serialization") {
    intercept[SerializationException] {
      val testMap = Map( "a" -> 2, "b" -> 2.0, "c" -> "d", "e" -> scala.collection.immutable.HashSet[String]("2"))
      JSONSerializer.serialize(testMap)
    }
  }
}

class JSONSerializationTests extends UnitFunSuite {
  test("verify primitive serialization") {
    val testList = List(1,2.0,false,null,2.000000000000099910010)
    JSONSerializer.serialize(testList)
  }
}

class JSONCircularTests extends UnitFunSuite {
  test("verify simple circular") {
    val testJson = "{ \"a\" : \"a\", \"b\" : true, \"c\" : 1, \"d\" : 2.2, \"e\" : null}"
    val parsedMap = net.liftweb.json.parse(testJson)
    val extractedMap = MapExtractor.extract[JSONSimpleTestClass](parsedMap)
    val serializedJSON : String = JSONSerializer.serialize(extractedMap)
    val reParsedMap = net.liftweb.json.parse(serializedJSON)
    assert (reParsedMap == parsedMap)
    val reExtractedMap = MapExtractor.extract[JSONSimpleTestClass](reParsedMap)
    assert(reExtractedMap == extractedMap)
    val reSerializedJSON : String = JSONSerializer.serialize(reExtractedMap)
    assert(reSerializedJSON == serializedJSON)
  }
  
  test("verify list circular") {
    val testJson = "{ \"a\" : \"b\", \"c\" : [1, 2, 3]}"
    val parsedMap = net.liftweb.json.parse(testJson)
    val extractedMap = MapExtractor.extract[JSONListTestClass](parsedMap)
    val serializedJSON : String = JSONSerializer.serialize(extractedMap)
    val reParsedMap = net.liftweb.json.parse(serializedJSON)
    assert (reParsedMap == parsedMap)
    val reExtractedMap = MapExtractor.extract[JSONListTestClass](reParsedMap)
    assert(reExtractedMap == extractedMap)
    val reSerializedJSON : String = JSONSerializer.serialize(reExtractedMap)
    assert(reSerializedJSON == serializedJSON)
  }
  
  test("verify nested circular") {
    val testJson = "{ \"a\" : \"b\", \"c\" : { \"d\" : \"e\" }}"
    val parsedMap = net.liftweb.json.parse(testJson)
    val extractedMap = MapExtractor.extract[JSONNestedParentTestClass](parsedMap)
    val serializedJSON : String = JSONSerializer.serialize(extractedMap)
    val reParsedMap = net.liftweb.json.parse(serializedJSON)
    assert (reParsedMap == parsedMap)
    val reExtractedMap = MapExtractor.extract[JSONNestedParentTestClass](reParsedMap)
    assert(reExtractedMap == extractedMap)
    val reSerializedJSON : String = JSONSerializer.serialize(reExtractedMap)
    assert(reSerializedJSON == serializedJSON)
  }
  
  test("verify nested list circular") {
    val testJson = "{ \"a\" : \"a\", \"b\" : [{\"c\" : \"d\"} , { \"c\" : \"e\" }]}"
    val parsedMap = net.liftweb.json.parse(testJson)
    val extractedMap = MapExtractor.extract[JSONNestedListParentTestClass](parsedMap)
    val serializedJSON : String = JSONSerializer.serialize(extractedMap)
    val reParsedMap = net.liftweb.json.parse(serializedJSON)
    assert (reParsedMap == parsedMap)
    val reExtractedMap = MapExtractor.extract[JSONNestedListParentTestClass](reParsedMap)
    assert(reExtractedMap == extractedMap)
    val reSerializedJSON : String = JSONSerializer.serialize(reExtractedMap)
    assert(reSerializedJSON == serializedJSON)
  }  
}

