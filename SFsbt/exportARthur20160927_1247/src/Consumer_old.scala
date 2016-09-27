import com.datastax.spark.connector.japi.CassandraJavaUtil
import com.datastax.spark.connector.writer.RowWriterFactory
import kafka.serializer.StringDecoder

/**
  * Created by user9 on 21/09/16.
  */
object sfp_load_mg {

  import java.awt.Color
  import scala.io.Source
  import javax.swing.JFrame
  import javax.swing.JPanel
  import java.awt.Dimension
  import java.awt.Toolkit
  import javax.swing.JFrame
  import java.awt.GridLayout
  import javax.swing.JPanel
  import org.apache.spark.SparkContext
  import org.apache.spark.rdd.RDD
  import org.apache.spark._
  import org.apache.spark.streaming._
  import org.apache.spark.streaming.StreamingContext._
  import org.apache.spark.streaming.kafka._
  import org.apache.spark.SparkContext._
  import com.datastax.spark.connector.cql.CassandraConnector
  import com.datastax.spark.connector._
  import org.apache.spark.{SparkConf, SparkContext}
  import com.datastax.spark.connector.japi.CassandraStreamingJavaUtil._

  def main(args: Array[String]) {
    val jarpath = Array("~/spark-cassandra-connector-assembly-1.6.0.jar")

    //val conf = new SparkConf().setMaster("local[2]").setAppName(getClass.getSimpleName).set("spark.executor.memory", "1g").set("spark.cassandra.connection.host", "127.0.0.1")
    val conf = new SparkConf(true).setAppName("scc").setMaster("local[2]").set("spark.cassandra.connection.host", "10.1.254.51,10.1.254.62,10.1.254.116")
    val sc = new SparkContext(conf.setJars(jarpath))
    val ssc = new StreamingContext(sc, Seconds(10))

    // kafka
    val kafkaParams = Map[String, String]("metadata.broker.list" -> "localhost:9092")
    val topics = Set("clickstream")
    val messages = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topics).map(_._2)


    CassandraConnector(conf).withSessionDo { session =>
      session.execute(s"CREATE KEYSPACE IF NOT EXISTS kafka26 WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': 3 }")
      session.execute(s"CREATE TABLE IF NOT EXISTS kafka26.longitude27 (X FLOAT PRIMARY KEY)")
      session.execute(s"TRUNCATE kafka26.longitude26")
    }


//    japi.CassandraStreamingJavaUtil.javaFunctions(messages)
//      .writerBuilder("kafka26", "longitude26",
//        CassandraJavaUtil.mapColumnTo()).withColumnSelector(SomeColumns("X")).saveToCassandra()

    //val msg_sc = messages.context

    messages.print()

    messages.foreachRDD(rdd => rdd.map(Tuple1(_)).saveToCassandra("kafka26", "longitude26", SomeColumns("X")))

    ssc.start() // Start the computation
    ssc.awaitTermination() // Wait for the computation to terminate




//    sc.textFile("/home/user9/IdeaProjects/kafkaConsumerSpark/src/main/scala-2.10/words")
//      .flatMap(_.split("\\s+"))
//      .map(word => (word.toLowerCase, 1))
//      .reduceByKey(_ + _)
//      .saveToCassandra("demo3", "wordcount3")
//    // print out the data saved from Spark to Cassandra
//    sc.cassandraTable("demo3", "wordcount3").collect.foreach(println)

    sc.stop()

  }
}