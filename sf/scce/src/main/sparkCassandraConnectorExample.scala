package main
import com.datastax.spark.connector._, org.apache.spark.SparkContext, org.apache.spark.SparkContext._, org.apache.spark.SparkConf

/**
  * Created by user14 on 21/09/16.
  */
object sparkCassandraConnectorExample {

  def main(args: Array[String]): Unit = {
    //sc.stop
    val conf = new SparkConf(true).setAppName("scc").setMaster("local[2]").set("spark.cassandra.connection.host", "10.1.254.51")
    val sc = new SparkContext(conf)
    println("***********1")

    val test_spark_rdd = sc.cassandraTable("system", "peers")
    println(test_spark_rdd.toString()) //.foreach(println)





    println("************2")
  }

}
