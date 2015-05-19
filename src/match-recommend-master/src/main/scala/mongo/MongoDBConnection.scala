package mongo

import com.mongodb.casbah.Imports._
import org.apache.log4j.Logger

/**
 * Created by szeyiu on 3/26/15.
 */
object MongoDBConnection{
  val logger = Logger.getLogger(getClass().getName());

  val dbName = "roomhunter"
  val ip = "121.40.49.49:27017/roomhunter"
  val uri = MongoClientURI("mongodb://roomhunter:f191d6b5b3152f0d520a9043a8de8fda73508bc1@"+ip)
  //val uri = MongoClientURI("mongodb://localhost:27017/roomhunter")
  lazy val mongoClient = MongoClient(uri)
  lazy val db = mongoClient(dbName)
  val histTableName = "hist"
  val recomTableName = "aptrecommend"
  logger.info("Connect to mongoDB: "+ip)
  logger.info("Current DB name: "+dbName)
  logger.info("History collection name: "+histTableName)
  logger.info("Apt recommend collection name: "+recomTableName)

  /*
  now we have following collections: apartments, users, wishlist
   */

  //a test function
  def main(args: Array[String])={
    var coll = db("users")
    val allItems = coll.find()

    println(allItems.size)
    allItems.foreach(e => println(e))

    val rst = coll.aggregate(List(
        MongoDBObject("$project" -> MongoDBObject("email" -> 1, "password"-> 1)),
        MongoDBObject(
          "$group" -> MongoDBObject("_id" -> MongoDBObject("email"->"$email", "password"->"$password"),
          "total"->MongoDBObject("$sum"->1))
        )
      )
    )
    rst.results.foreach(e => println(e))

  }
}
