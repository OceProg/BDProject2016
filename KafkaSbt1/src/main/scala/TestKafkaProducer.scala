/**
  * Created by user14 on 19/09/16.
  */

package fr.univalence.kafkatraining.testapi

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Properties
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}


object TestKafkaProducer {

  def kafkaProps = {
    val props = new Properties()

    def put(k:String, v:Any) = props.put(k,v.toString)

    put("bootstrap.servers", "localhost:9092")
    put("acks", "all")
    put("retries",0)
    put("batch.size", 16384)
    put("linger.ms", 1)
    put("buffer.memory", 33554432)
    put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    put("producer.type", "sync")

    props
  }

  def main(args: Array[String]) {

    val producer: KafkaProducer[String, String] = new KafkaProducer[String, String](kafkaProps)

//    val now : Calendar  = Calendar.getInstance()
//    val year = now.get(Calendar.YEAR)
//    val month = now.get(Calendar.MONTH) + 1 // Note: zero based!
//    val day = now.get(Calendar.DAY_OF_MONTH)
//    val hour = now.get(Calendar.HOUR_OF_DAY)
//    val minute = now.get(Calendar.MINUTE)
//    val second = now.get(Calendar.SECOND)
//    val millis = now.get(Calendar.MILLISECOND)
    //System.out.printf("%d-%02d-%02d %02d:%02d:%02d.%03d", year, month, day, hour, minute, second, millis);


    //val sdf : SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
    println("1")

    ( 1  to 10 ).foreach { x =>
      println("2")
//      val now: Date = new Date()
//      val strDate = sdf.format(now)
//      producer.send(new ProducerRecord[String, String]("clickstream", null, strDate))
//      println("sent", strDate)
      producer.send(new ProducerRecord[String, String]("clickstream", null, "boop"+x))
      println("3")
      println("sent", "boop"+x)
      //Thread sleep 1
    }


    producer.close()
  }

}
