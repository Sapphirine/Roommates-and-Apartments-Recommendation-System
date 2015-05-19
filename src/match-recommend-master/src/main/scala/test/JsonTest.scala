package test
import org.json4s._
import org.json4s.native.JsonMethods._

/**
 * Created by szeyiu on 3/26/15.
 */
object JsonTest {

  def main(args: Array[String])={
    val jsonStr = """{"id": "20150320", "userid": "xxxx", "self":{"a":"1","b":"1","c":"1","d":"1","e":"1","f":"1","g":"1","h":"1","i":"1","j":"1"}, "target":{"a":"2","b":"2","c":"2","d":"2","e":"2","f":"2","g":"2","h":"2","i":"2","j":"2"}}"""
    val jsonObj = parse(jsonStr)

    //println(jsonObj)


    val selfVector = getMetric("self", jsonObj)
    val targetVector = getMetric("target", jsonObj)

    println(selfVector)
    println(targetVector)
  }

  def getMetric(attr: String, jsonObj: JValue) = for(
    JObject(obj) <- jsonObj;
    JField(attr, JObject(lstObj)) <- obj;
    JField(_,JString(s)) <- lstObj) yield
    s.toDouble

}
