package listener

import java.util.concurrent.Executors
import javax.jms.{MessageListener, Message}
import job.{MatchJob, TestJob}
import mq.MessageQueue
import org.apache.log4j.Logger

/**
 * Created by szeyiu on 3/27/15.
 */
object MatchListener extends MessageListener {
  val logger = Logger.getLogger(getClass().getName());

  val pool = Executors.newCachedThreadPool()
  val MATCH_TOPIC = "MATCH"

  def start(): Unit ={
    MessageQueue.setListener(MATCH_TOPIC, this)
  }

  override def onMessage(message: Message) = {
    logger.info("Receive a msg with topic: " + MATCH_TOPIC)
    pool.execute(new MatchJob(message))
  }

}
