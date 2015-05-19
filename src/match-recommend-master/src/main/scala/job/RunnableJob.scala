package job

import javax.jms._
import org.apache.log4j.Logger

/**
 * Created by szeyiu on 3/26/15.
 */
abstract class RunnableJob(msg: Message) extends Runnable{
  val logger = Logger.getLogger(getClass().getName());

  def run(message: Message): Unit = {
    logger.info("Abstract job: Do nothing")
  }

  override def run(): Unit = {
    run(msg)
  }
}
