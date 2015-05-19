package job
import javax.jms._
import org.apache.log4j.Logger
import org.json4s._
import org.json4s.native.JsonMethods._
/*
 * Created by szeyiu on 4/19/15.
 */
class MatchTestJob (val msg: Message) extends RunnableJob(msg){
    override val logger = Logger.getLogger(getClass().getName());

    implicit lazy val formats = DefaultFormats
    override def run(message: Message): Unit = message match{
        case m: TextMessage => goTest(m.getText)
    }
    def goTest(jsonStr: String): Unit ={
        /**
         * jsonStr equals:
         * {"id": "20150320", "userid": "xxxx", "self":{"a":"1","b":"1","c":"1","d":"1","e":"1","f":"1","g":"1","h":"1","i":"1","j":"1"}, "target":{"a":"2","b":"2","c":"2","d":"2","e":"2","f":"2","g":"2","h":"2","i":"2","j":"2"}}
         */
        logger.info(jsonStr)
        val json = parse(jsonStr)
        val pref = json.extract[Preference]
        //JsonFormat.as[Preference](/* file | string | reader | byte array | input stream | URL */ jsonStr)
        logger.info("MatchTestJob Read JSON: ")
        logger.info(pref.toString)

        /**
         * pref is:
         * Preference(20150320,xxxx,Map(e -> 1, j -> 1, f -> 1, a -> 1, i -> 1, b -> 1, g -> 1, c -> 1, h -> 1, d -> 1),Map(e -> 2, j -> 2, f -> 2, a -> 2, i -> 2, b -> 2, g -> 2, c -> 2, h -> 2, d -> 2))
         */
    }
}
case class Preference(userid: String, self: Map[String, String], target: Map[String, String])

