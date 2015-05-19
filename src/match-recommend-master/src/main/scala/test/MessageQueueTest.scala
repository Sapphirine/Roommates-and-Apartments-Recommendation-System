package test

import javax.jms._

import listener.TestListener
import mq._
/**
 * Created by szeyiu on 3/26/15.
 */
object MessageQueueTest {
  def main(args: Array[String])={
    TestListener.start()

    println("Hello World")
    for(i <- 0 until 10) {
      println("send a message")
      val jsonStr = """{"id": "20150320", "userid": "xxxx", "self":{"a":"1","b":"1","c":"1","d":"1","e":"1","f":"1","g":"1","h":"1","i":"1","j":"1"}, "target":{"a":"2","b":"2","c":"2","d":"2","e":"2","f":"2","g":"2","h":"2","i":"2","j":"2"}}"""
      MessageQueue.sendMessage(TestListener.TEST_TOPIC, jsonStr)
      Thread sleep 500
    }
    println("close")
  }
}
