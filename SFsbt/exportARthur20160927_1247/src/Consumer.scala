import com.datastax.spark.connector.writer.RowWriterFactory
import kafka.serializer.StringDecoder

/**
  * Created by user9 on 21/09/16.
  */
object Consumer {

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

  def main(args: Array[String]) {
    val jarpath = Array("~/spark-cassandra-connector-assembly-1.6.0.jar")
    val conf = new SparkConf(true).setAppName("scc").setMaster("local[2]").set("spark.cassandra.connection.host", "10.1.254.51,10.1.254.62,10.1.254.116")
    val sc = new SparkContext(conf.setJars(jarpath))
    val ssc = new StreamingContext(sc, Seconds(10))


    // kafka
    val kafkaParams = Map[String, String]("metadata.broker.list" -> "localhost:9092")
    val topics = Set("clickstream")
    val messages = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topics).map(_._2)


    CassandraConnector(conf).withSessionDo { session =>
      session.execute(s"CREATE KEYSPACE IF NOT EXISTS full1 WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': 3 }")
      session.execute(s"CREATE TABLE IF NOT EXISTS full1.3col (In INT PRIMARY KEY, Category TEXT, Descript TEXT)")
      session.execute(s"TRUNCATE full1.3col")
    }


    messages.foreachRDD { rdd => rdd.map(Tuple1(_)).saveToCassandra("full1", "3col") }
    messages.print()

    ssc.start() // Start the computation
    ssc.awaitTermination() // Wait for the computation to terminate


//    CassandraConnector(conf).withSessionDo { session =>
//      session.execute(s"CREATE KEYSPACE IF NOT EXISTS kafka26 WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': 3 }")
//      session.execute(s"CREATE TABLE IF NOT EXISTS kafka26.longitude26 (X TEXT PRIMARY KEY, Y TEXT, )")
//      session.execute(s"TRUNCATE kafka26.longitude26")
//    }
//
//
//    messages.foreachRDD { rdd => rdd.map(Tuple1(_)).saveToCassandra("kafka26", "longitude26") }
//    messages.print()



//    val msg_sc = messages.context.sparkContext



    //val nn = msg_sc.parallelize(messages)


    //messages.map(_).countByValue().saveToCassandra("demo", "wordcount")


    //messages.foreachRDD(rdd => rdd.saveToCassandra("kafka1", "longitude1"))

//    msg_sc
//      .parallelize(1 to 1,1)
//      .saveToCassandra("kafka1", "longitude1")//.cassandraTable("kafka1", "longitude1").collect.foreach(println)

//    japi.CassandraStreamingJavaUtil.javaFunctions(messages)
//      .writerBuilder("kafka1", "longitude1", RowWriterFactory().withColumnSelector(SomeColumns("X")).saveToCassandra()

//    val tt = sc.textFile("/home/user9/IdeaProjects/kafkaConsumerSpark/src/main/scala-2.10/words")

//
//    sc.textFile("/home/user9/IdeaProjects/kafkaConsumerSpark/src/main/scala-2.10/words")
//      .flatMap(_.split("\\s+"))
//      .map(word => (word.toLowerCase, 1))
//      .reduceByKey(_ + _)
//      .saveToCassandra("demo3", "wordcount3")
//    // print out the data saved from Spark to Cassandra
//    sc.cassandraTable("demo3", "wordcount3").collect.foreach(println)

    //sc.stop()




  }
}