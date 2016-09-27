/**
  * Created by user14 on 23/09/16.
  */

import org.apache.spark.SparkContext._
import com.datastax.spark.connector.cql.CassandraConnector
import com.datastax.spark.connector._
import org.apache.spark
import org.apache.spark.{SparkConf, SparkContext}
import java.io._
import java.util.zip.GZIPInputStream

object sfpd_load {

  //to read from gz
  def gis(s: String) = new GZIPInputStream(new BufferedInputStream(new FileInputStream(s)))

  //print N lines from a data set
  def printNlines[T](data: Array[Array[T]], start: Int, end: Int): Unit = {
    for (i <- start to end - 1) {
      println(data.toList(i).toList)
    }
  }

  def main(args: Array[String]): Unit = {
    //sc.stop
    val jarpath = Array("~/spark-cassandra-connector-assembly-1.6.0.jar")
    //val conf = new SparkConf(true).setAppName("scc").set("spark.cassandra.connection.host", "10.1.254.51,10.1.254.62,10.1.254.116")
    val conf = new SparkConf(true).setAppName("scc").setMaster("local[2]").set("spark.cassandra.connection.host", "10.1.254.51,10.1.254.62,10.1.254.116")
    //val conf = new SparkConf(true).setAppName("scc").setMaster("local[2]").set("spark.cassandra.connection.host", "10.1.254.51")
    //val conf = new SparkConf(true).setAppName("scc").setMaster("spark://10.1.254.62:7077").set("spark.cassandra.connection.host", "10.1.254.51,10.1.254.62,10.1.254.116")
    // 1 - remove this when doing a spark-submit
    val sc = new SparkContext(conf.setJars(jarpath))
    //println(sc)
    println("******1")

    val path = "/home/user14/Documents/projet/data/"
    val file_name1 = "sfpd_total.csv"
    val inputFile = sc.textFile(path+file_name1)
    val header = inputFile.first() //extract header
    val varNames_source_sc = header.split(",")
    val data_source_sc = inputFile.filter(row => row != header)
    println(varNames_source_sc.size)
    println(varNames_source_sc.toList)

    CassandraConnector(conf).withSessionDo { session =>
      session.execute(s"CREATE KEYSPACE IF NOT EXISTS sf WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': 3 }")
      session.execute(s"CREATE TABLE IF NOT EXISTS sf.sfpdtotal (IncidntNum INT PRIMARY KEY, Category TEXT, Descript TEXT, DayOfWeek TEXT, Date TEXT, Time TEXT, PdDistrict TEXT, Resolution TEXT, Address TEXT, X TEXT, Y TEXT, Location TEXT, PdId TEXT, ZipCode TEXT, X1 INT, American_Indian_population FLOAT, Asian_population FLOAT, Average_Adjusted_Gross_Income_AGI_in_2012 FLOAT, Average_household_size FLOAT, Black_population FLOAT, Estimated_zip_code_population_in_2013 FLOAT, Females FLOAT, Hispanic_or_Latino_population FLOAT, Houses_and_condos FLOAT, Land_area FLOAT, Males FLOAT, Mar_2016_cost_of_living_index_in_zip_code FLOAT, Median_resident_age FLOAT, Native_Hawaiian_and_Other_Pacific_Islander_population FLOAT, Population_density_people_per_square_mile FLOAT, Renter_occupied_apartments FLOAT, Residents_with_income_below_50_of_the_poverty_level_in_2013 FLOAT, Salary_wage FLOAT, Some_other_race_population FLOAT, Two_or_more_races_population FLOAT, Water_area FLOAT, White_population FLOAT, Zip_code_population_in_2000 FLOAT, Zip_code_population_in_2010 FLOAT)")
     }

      //[int -> INT,str -> TEXT,str,str,str,str,str,str,str,str,str,str,int,str,int,float -> FLOAT,float,float,float,float,float,float,float,float,float,float,float,float,float,float,float,float,float,float,float,float,float,float,float,int,int]


    println("******2")
    val varNames_col = SomeColumns("IncidntNum", "Category", "Descript", "DayOfWeek", "Date", "Time", "PdDistrict", "Resolution", "Address", "X", "Y", "Location", "PdId", "ZipCode", "X1", "American_Indian_population", "Asian_population", "Average_Adjusted_Gross_Income_AGI_in_2012", "Average_household_size", "Black_population", "Estimated_zip_code_population_in_2013", "Females", "Hispanic_or_Latino_population", "Houses_and_condos", "Land_area", "Males", "Mar_2016_cost_of_living_index_in_zip_code", "Median_resident_age", "Native_Hawaiian_and_Other_Pacific_Islander_population", "Population_density_people_per_square_mile", "Renter_occupied_apartments", "Residents_with_income_below_50_of_the_poverty_level_in_2013", "Salary_wage", "Some_other_race_population", "Two_or_more_races_population", "Water_area", "White_population", "Zip_code_population_in_2000", "Zip_code_population_in_2010")
    data_source_sc.saveToCassandra("sf", "sfpdtotal", varNames_col)


    println("******3")
//
//    CassandraConnector(conf).withSessionDo { session =>
//      session.execute(s"CREATE KEYSPACE IF NOT EXISTS demo WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': 3 }")
//      session.execute(s"CREATE TABLE IF NOT EXISTS demo.wordcount (word TEXT PRIMARY KEY, count COUNTER)")
//      session.execute(s"TRUNCATE demo.wordcount")
//    }
//
//    sc.textFile("/home/user14/Documents/projet/data/words")
//      .flatMap(_.split("\\s+"))
//      .map(word => (word.toLowerCase, 1))
//      .reduceByKey(_ + _)
//      .saveToCassandra("demo", "wordcount")
//
//    // print out the data saved from Spark to Cassandra
//    sc.cassandraTable("demo", "wordcount").collect.foreach(println)

    sc.stop()

  }

}
