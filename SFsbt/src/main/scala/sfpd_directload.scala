import com.datastax.spark.connector.cql.CassandraConnector
import kafka.serializer.StringDecoder
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by user14 on 27/09/16.
  */
object sfpd_directload {
  def main(args: Array[String]) {
    val jarpath = Array("~/spark-cassandra-connector-assembly-1.6.0.jar")
    val conf = new SparkConf(true).setAppName("scc").setMaster("local[2]").set("spark.cassandra.connection.host", "10.1.254.51,10.1.254.62,10.1.254.116")
    val sc = new SparkContext(conf.setJars(jarpath))

    val csv = sc.textFile("~/Documents/projet/data/sfpd_total_10.csv")


    CassandraConnector(conf).withSessionDo { session =>
      session.execute(s"CREATE KEYSPACE IF NOT EXISTS full1 WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': 3 }")
      session.execute(s"CREATE TABLE IF NOT EXISTS full1.3col (In INT PRIMARY KEY, Category TEXT, Descript TEXT)")
      session.execute(s"TRUNCATE full1.3col")
    }




    //messages.foreachRDD { rdd => rdd.map(Tuple1(_)).saveToCassandra("full1", "3col") }


  }

}
