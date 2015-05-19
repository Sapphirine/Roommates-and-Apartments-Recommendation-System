package model

import java.io.{PrintWriter, File}
import java.util.concurrent.Executors
import com.mongodb.casbah.Imports._
import com.thoughtworks.xstream.converters.basic.LongConverter
import org.apache.log4j.Logger
import org.apache.mahout.cf.taste.common.Refreshable
import org.apache.mahout.cf.taste.impl.common.{FastIDSet, LongPrimitiveIterator}
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity
import org.apache.mahout.cf.taste.model._
import mongo.MongoDBConnection
import org.apache.mahout.cf.taste.impl.common.FastByIDMap
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood
import org.apache.mahout.cf.taste.recommender.{RecommendedItem, UserBasedRecommender}
import org.apache.mahout.cf.taste.similarity.UserSimilarity
import common.StringEncoder._
/**
 * Created by szeyiu on 4/3/15.
 * DB schema for hist is : {userid: xxx, aptid: yyy, timestamp: zzz}
 */
object MongoDataModel{
  val logger = Logger.getLogger(getClass().getName());

  var aptDataModel: DataModel = null
  logger.info("Initializing data model for apt recommend...")
  refreshAptDataModel
  class Refresher extends Runnable{
    override def run(): Unit = {
      while (true) {
        logger.info("data model for apt recommend refreshes every 2min...")
        Thread sleep 1000 * 60 * 2 //refresh hist every 2 min
        refreshAptDataModel
      }
    }
  }
  Executors.newCachedThreadPool().execute(new Refresher)


  def refreshAptDataModel: Unit = {
    val histTableName = MongoDBConnection.histTableName
    val histColl = MongoDBConnection.db(histTableName)
    val USER_ID = "userId"//NEED TO CHANGE IN STAGING ENV
    val APT_ID = "aptId"
    val APT_LIST = "aptList"
    val rst = histColl.aggregate(List(
      MongoDBObject("$project" -> MongoDBObject(USER_ID -> 1, APT_ID-> 1)),
      MongoDBObject(
        "$group" -> MongoDBObject("_id" -> MongoDBObject(USER_ID->("$"+USER_ID), APT_ID->("$"+APT_ID)),
          "score" -> MongoDBObject("$sum"->1))
      ))
    )
    if(rst.results.size==0) return
    //println(rst.results.size)
    val TMP_FILE = "tmp.csv"
    val writer = new PrintWriter(new File(TMP_FILE))
    logger.info("writing data model in tmp file: "+TMP_FILE)
    try {
      rst.results.foreach(row => {
        //here need long!!!! A PROBLEM!!!
        if(row.get("_id").asInstanceOf[DBObject].containsField(USER_ID) &&
          row.get("_id").asInstanceOf[DBObject].containsField(APT_ID)) {
          writer.write(string2Long(row.get("_id").asInstanceOf[DBObject].get(USER_ID).toString) + ",")
          writer.write(string2Long(row.get("_id").asInstanceOf[DBObject].get(APT_ID).toString) + ",")
          writer.write(row.get("score").toString + "\n")
        }
      })
    } catch{
      case _ => {writer.close(); logger.warn("ERROR WHEN LOAD REC MODEL!!!");return;}
    }
    writer.close()
    try {
      val model: DataModel = new FileDataModel(new File(TMP_FILE))
      aptDataModel = model
    } catch{
      case _ => {logger.warn("ERROR WHEN READING REC MODEL FILE!!!");return;}
    }
  }

  def writeRecommend2DB(userId: String, aptIdList: List[String]): Unit ={
    val recomTableName = MongoDBConnection.recomTableName
    val APT_LIST = "aptList"
    val USER_ID = "userId"
    logger.info("writing recommend to DB..."+USER_ID+": "+USER_ID+" /"+APT_LIST+": "+aptIdList)
    val recomColl = MongoDBConnection.db(recomTableName)
    var aptMongoList: MongoDBList = new MongoDBList()
    aptMongoList.insertAll(0,aptIdList)
    recomColl.update(
    MongoDBObject(USER_ID->userId),
    MongoDBObject(USER_ID->userId)++(APT_LIST->aptMongoList), true)
  }
}
