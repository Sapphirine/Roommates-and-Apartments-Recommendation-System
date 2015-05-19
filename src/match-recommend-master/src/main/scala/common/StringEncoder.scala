package common

import scala.collection.immutable.HashMap

/**
 * Created by szeyiu on 4/4/15.
 */

object StringEncoder {
  var char2id: HashMap[Char, Long] = new HashMap[Char, Long]
  var id2char: List[Char] = List('*')
  for(i <- 1 to 26){
    val ch: Char = ('a'+i-1).asInstanceOf[Char]
    id2char=id2char:+ch
    char2id = char2id + (ch->i)
  }
  for(i <- 1 to 26){
    val ch: Char = ('A'+i-1).asInstanceOf[Char]
    id2char=id2char:+ch
    char2id = char2id + (ch->(i+26))
  }
  for(i <- 1 to 10){
    var ch: Char = ('0'+i-1).asInstanceOf[Char]
    id2char=id2char:+ch
    char2id = char2id + (ch->(i+52))
  }

  def string2Long(str9: String): Long = {
    var id: Long = 0
    str9.toList.foreach(c => {
      id *= 64
      id += char2id(c)
    })
    id
  }

  def long2String(id: Long): String = {
    var chlstRev: List[Char] = Nil
    var idd = id
    while(idd!=0){
      val curid = (idd%64).asInstanceOf[Int]
      idd /= 64
      chlstRev = chlstRev:+id2char(curid)
    }
    chlstRev.reverse mkString
  }

/*
  def main(args: Array[String])= {
    val str = "0000000"
    val id = string2Long(str)
    println(id)
    println(long2String(id))
  }
*/

}
