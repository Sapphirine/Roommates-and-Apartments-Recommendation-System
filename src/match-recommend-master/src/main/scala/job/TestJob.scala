package job
import javax.jms._
import job.RunnableJob
import org.apache.log4j.Logger

/**
 * Created by szeyiu on 3/26/15.
 */
class TestJob(val msg: Message) extends RunnableJob(msg){
  override val logger = Logger.getLogger(getClass().getName());

  override def run(message: Message): Unit = message match {
    case m: TextMessage => val msgId = message.getJMSMessageID
      logger.info("TestJob: "+ msgId + "\ntext:"+m.getText)
      logger.info(msgId+": sleep for 10s")
      Thread sleep 10000
      println(msgId+": weak up and finish")
    case _ => println("Unsupported msg type")
  }

}
