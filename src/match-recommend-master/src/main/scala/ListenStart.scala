import listener._
import model.MongoDataModel
import mq.MessageQueue
import org.apache.log4j.Logger

/**
 * Created by szeyiu on 3/27/15.
 */
object ListenStart {
  val logger = Logger.getLogger(getClass().getName());

  def main(args: Array[String])={
    //TestListener.start()
    //logger.info("Test Listener started!")
    MatchListener.start()
    logger.info("Match Listener started!")
    RecomListener.start()
    logger.info("Recommend Listener Started!")
    logger.info("All things are set up... Listening for jobs...")
    //mock data
    //val str = """{"id": "20150320", "userid": "xxxx", "self":{"a":"1","b":"1","c":"1","d":"1","e":"1","f":"1","g":"1","h":"1","i":"1","j":"1"}, "target":{"a":"2","b":"2","c":"2","d":"2","e":"2","f":"2","g":"2","h":"2","i":"2","j":"2"}}"""
/*    for(i <- 0 until 10) {
      println("send a message")
      MessageQueue.sendMessage(RecomListener.MATCH_TOPIC, "3qjEMmn1k")
      Thread sleep 500
    }*/
  }
}
