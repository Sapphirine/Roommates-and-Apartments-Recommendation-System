package job

import javax.jms._
import common.StringEncoder._
import model.MongoDataModel
import org.apache.log4j.Logger
import org.apache.mahout.cf.taste.impl.neighborhood._
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender
import org.apache.mahout.cf.taste.impl.similarity._
import org.apache.mahout.cf.taste.model.DataModel
import org.apache.mahout.cf.taste.neighborhood._
import org.apache.mahout.cf.taste.recommender.{RecommendedItem, UserBasedRecommender}
import org.apache.mahout.cf.taste.similarity.UserSimilarity

import scala.collection.JavaConverters._
/**
 * Created by szeyiu on 4/4/15.
 */
class RecomJob(val msg: Message) extends RunnableJob(msg){
  override val logger = Logger.getLogger(getClass().getName());

  val threshold: Int = 5
  val topK = 30


  override def run(message: Message): Unit = message match {
    case m: TextMessage => goRecom(m.getText)
    case m: BytesMessage => {
      var data: Array[Byte] = new Array[Byte](m.getBodyLength.asInstanceOf[Int])
      m.readBytes(data)
      goRecom(new String(data))
    }
    case _ => logger.info("Unsupported")
  }

  def goRecom(userId: String): Unit= {
    logger.info("Recommending for user: "+userId)
    val model :DataModel = MongoDataModel.aptDataModel
    if(model==null) return

    val similarity: UserSimilarity = new EuclideanDistanceSimilarity(model)
    val neighborhood: UserNeighborhood = new NearestNUserNeighborhood(threshold, similarity, model)
    val recommender: UserBasedRecommender = new GenericUserBasedRecommender(model, neighborhood, similarity)
    val userLong = string2Long(userId)
    neighborhood.getUserNeighborhood(userLong).foreach(e => println(e))
    val recommendations: List[RecommendedItem] = recommender.recommend(userLong, topK).asScala.toList
    val aptIdList = recommendations.map(rec=> long2String(rec.getItemID))
    MongoDataModel.writeRecommend2DB(userId, aptIdList)
    logger.info("user: "+userId+ ", size of recom: "+recommendations.size)
    recommendations.foreach(e => logger.info(long2String(e.getItemID)+", "+e.getValue))
  }

}
