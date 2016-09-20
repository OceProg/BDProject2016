/**
  * Created by user14 on 19/09/16.
  */

package fr.univalence.kafkatraining.testapi
import java.util.Collection
import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.clients.consumer.{KafkaConsumer, ConsumerRecords}
import org.apache.kafka.common.TopicPartition


object TestKafkaConsumer {

  def kafkaProps = {
    val props = new Properties(TestKafkaProducer.kafkaProps)

    def put(k:String, v:Any) = props.put(k,v.toString)

    put("group.id", "test-consumer")
    put("bootstrap.servers", "localhost:9092")
    put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")

    props
  }

  def main(args: Array[String]) {

    import scala.collection.JavaConversions._

    val consumer: KafkaConsumer[String, String] = new KafkaConsumer[String, String](kafkaProps)

//    consumer.subscribe(util.Arrays.asList("clickstream"))

    val topicsPartition: java.util.Collection[TopicPartition] =
      consumer.partitionsFor("clickstream").map(p => {new TopicPartition(p.topic, p.partition)})


    consumer.assign(topicsPartition)

    consumer.poll(0)

    consumer.seekToBeginning(topicsPartition)

    while(true) {
      val records: ConsumerRecords[String, String] = consumer.poll(100)

      for (record <- records.iterator){

        //if (record.value().contains("click")){ //check only in values
//        if (record.toString.contains("click")){ //check whole line
//            println(record)
//
//        }
        println(record)
      }

    }

      consumer.commitSync()
  }
}


