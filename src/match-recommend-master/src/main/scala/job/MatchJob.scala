package job

import javax.jms._
import org.apache.log4j.Logger
import org.json4s._
import org.json4s.native.JsonMethods._
//import com.codahale.jerkson.Json._
import scala.collection.mutable.PriorityQueue

/**
 * Created by szeyiu on 3/26/15.
 */
class MatchJob(val msg: Message) extends RunnableJob(msg){

  override val logger = Logger.getLogger(getClass().getName());
  case class Preference(userId: String, self: Map[String, Double], target: Map[String, Double])

  override def run(message: Message): Unit = message match {
    case m: TextMessage => goMatch(m.getText)
    case m: BytesMessage => {
      var data: Array[Byte] = new Array[Byte](m.getBodyLength.asInstanceOf[Int])
      m.readBytes(data)
      goMatch(new String(data))
    }
    case _ => logger.info("Unsupported")
  }


  def goMatch(jsonStr: String): Unit = {
    logger.info("doing roommates matching... json: "+ jsonStr)
    val jsonObj = parse(jsonStr)
    val selfVector = getMetricFromMQ("self", jsonObj)
    val targetVector = getMetricFromMQ("target", jsonObj)
    val curVector = (selfVector._1, selfVector._2 ++ targetVector._2)
    lazy val jsonList = getJsonFromDB()
    val heap = new PriorityQueue[(String, Double)]()(Ordering.by(t1 => t1._2))
    jsonList.foreach(dbJsonObj => {
      val tmp = getMetricFromDB(dbJsonObj)
      val dbVector = (tmp._1, tmp._2 ++ selfVector._2)
      val sim = (tmp._1, computeSim(curVector._2, dbVector._2))
      if(!tmp._1.equals(curVector._1)) heap.enqueue(sim)
    })

    logger.info(selfVector._1 + " Matches: ")
    for(i <- 0 until 20){
      if(heap.isEmpty) return
      logger.info(heap.dequeue().toString())
    }
  }

  def getMetricFromMQ(attr: String, jsonObj: JValue): (String, List[Double]) = {
    val a: String =
      jsonObj.filterField {
        case JField("userid", _) => true
        case _ => false
      }.map(e => e match {
        case (_, JString(s)) => s
        case _ => ""
      }
        ).foldLeft("") { (m, n) => m + n }

    val b: List[Double] =
      for (
        JObject(obj) <- jsonObj;
        JField(attr, JObject(lstObj)) <- obj;
        JField(_, JString(s)) <- lstObj) yield
      s.toDouble

    (a, b)
  }

  /**
   * walk around using mock function
   * @param jsonObj
   * @return
   */
  def getMetricFromDB(jsonObj: JValue): (String, List[Double]) = getMetricFromMQ("preference", jsonObj)

  /**
   * Walk around using mock data
   * @return
   */
  def getJsonFromDB(): List[JValue] = {
    var str = """ {"id": "1", "userid": "1", "preference":{"a":"1","b":"1","c":"1","d":"0","e":"11","f":"1","g":"1","h":"0","i":"1","j":"1"}}"""
    var rst: List[JValue] = List(parse(str))
    str = """ {"id": "2", "userid": "2", "preference":{"a":"2","b":"0","c":"8","d":"1","e":"12","f":"1","g":"1","h":"1","i":"1","j":"-1"}}"""
    rst = rst ++ List(parse(str))
    str = """{"id": "3", "userid": "3", "preference":{"a":"3","b":"3","c":"1","d":"2","e":"13","f":"1","g":"1","h":"1","i":"0","j":"-1"}} """
    rst = rst ++ List(parse(str))
    str = """{"id": "4", "userid": "4", "preference":{"a":"4","b":"1","c":"6","d":"1","e":"1","f":"1","g":"1","h":"0","i":"-1","j":"1"}}"""
    rst = rst ++ List(parse(str))
    str = """{"id": "5", "userid": "5", "preference":{"a":"7","b":"5","c":"1","d":"4","e":"14","f":"12","g":"1","h":"1","i":"1","j":"1"}}"""
    rst = rst ++ List(parse(str))
    str = """{"id": "6", "userid": "6", "preference":{"a":"2","b":"1","c":"4","d":"1","e":"1","f":"1","g":"1","h":"1","i":"0","j":"-1"}}"""
    rst = rst ++ List(parse(str))

    rst
  }

  def computeSim(a: List[Double], b: List[Double]) =
    a.zip(b).map(e => e._1 * e._2).foldLeft(0.0){(m,n) => m+n} /
      (a.foldLeft(0.0){(m,n) => m + n*n} * b.foldLeft(0.0){(m,n) => m + n*n})



}

/**
 * This object is for testing
 */
object MatchJob{
  def main(args: Array[String]) = {
    val str = """{"id": "20150320", "userid": "xxxx", "self":{"a":"1","b":"1","c":"1","d":"1","e":"1","f":"1","g":"1","h":"1","i":"1","j":"1"}, "target":{"a":"2","b":"2","c":"2","d":"2","e":"2","f":"2","g":"2","h":"2","i":"2","j":"2"}}"""
    val j = new MatchJob(null)
    j.goMatch(str)
  }
}
