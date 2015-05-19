package listener

import java.util.concurrent.Executors
import javax.jms.{Message, MessageListener}
import job.{RecomBatchJob, RecomJob, MatchJob}
import mq.MessageQueue
import org.apache.log4j.Logger

/**
 * Created by szeyiu on 4/4/15.
 */
object RecomListener  extends MessageListener{
  val logger = Logger.getLogger(getClass().getName());

  val pool = Executors.newCachedThreadPool()
  val REC_TOPIC = "RECOMMEND"

  def start(): Unit ={
    MessageQueue.setListener(REC_TOPIC, this)
    pool.execute(new RecomBatchJob)
  }
  override def onMessage(message: Message) = {
    logger.info("Recom Listener: Receive a msg with topic: " + REC_TOPIC)
    pool.execute(new RecomJob(message))
  }

}
