package job

import javax.jms.Message
import model.MongoDataModel
import org.apache.log4j.Logger
import org.apache.mahout.cf.taste.model.DataModel
import common.StringEncoder._
/**
 * Created by szeyiu on 4/16/15.
 */
class RecomBatchJob extends RecomJob(null){
  override val logger = Logger.getLogger(getClass().getName());

  override def run(message: Message): Unit ={
    while(true){
      recomAll()
      Thread sleep 2*60*1000 //calculate all recom for every 2 min
    }
  }

  def recomAll(): Unit ={
    logger.info("doing batch recommend...")
    val model :DataModel = MongoDataModel.aptDataModel
    if(model==null) return
    val userIter = model.getUserIDs
    while(userIter.hasNext) {goRecom(long2String(userIter.nextLong()))}
  }
}
