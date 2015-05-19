package mq

import mongo.MongoDBConnection._
import org.apache.activemq.ActiveMQConnectionFactory
import javax.jms.Session
import javax.jms.Destination
import javax.jms.MessageProducer
import javax.jms.DeliveryMode
import javax.jms.TextMessage
import javax.jms.MessageListener

import org.apache.log4j.Logger

object MessageQueue {
  val logger = Logger.getLogger(getClass().getName());
  val url = "tcp://121.40.156.23:61616"//testing env
  lazy val messageQueue = new MessageQueue(url)
  logger.info("Message Queue, url: "+url)

  def sendMessage(topicId: String, messageText: String) = {
    var session: Session = null
    try {
      session = messageQueue.connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
      val producer: MessageProducer = session.createProducer(null)
      val destination: Destination = session.createTopic(topicId)
      producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT)
      val message: TextMessage = session.createTextMessage(messageText)
      producer.send(destination,message)
    } finally {
      if (session != null) {
        session.close()
      }
    }
  }

  def setListener(topicId : String,listener: MessageListener) = {
    val session = messageQueue.connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
    val destination = session.createTopic(topicId)
    val messageConsumer = session.createConsumer(destination)
    messageConsumer.setMessageListener(listener)
  }

}

class MessageQueue(url: String = "tcp://localhost:61616") {
  lazy val factory = new ActiveMQConnectionFactory(url)
  var connection = factory.createConnection()
  connection.start()
}