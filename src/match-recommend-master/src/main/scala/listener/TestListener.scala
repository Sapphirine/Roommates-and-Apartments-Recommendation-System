package listener

import javax.jms._
import job.TestJob
import java.util.concurrent.Executors
import mq._
import job._
import org.apache.log4j.Logger

/**
 * Created by szeyiu on 3/26/15.
 */
object TestListener extends MessageListener{
  val logger = Logger.getLogger(getClass().getName());

  val pool = Executors.newCachedThreadPool()
  val TEST_TOPIC = "TestTopic"


  def start(): Unit ={
    MessageQueue.setListener(TEST_TOPIC, this)

  }
  override def onMessage(message: Message) = {
    logger.info("TestListener: Receive a msg with topic: " + TEST_TOPIC)
    pool.execute(new MatchTestJob(message))
  }
}
