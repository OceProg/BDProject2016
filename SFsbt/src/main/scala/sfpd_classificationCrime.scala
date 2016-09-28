import org.apache.spark.{SparkConf, SparkContext}
import org.slf4j.LoggerFactory
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.sql.{Row, DataFrame, SQLContext}
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
  * Created by user14 on 27/09/16.
  */
object sfpd_classificationCrime {

  val logger = LoggerFactory.getLogger(getClass)
  val csvFormat = "com.databricks.spark.csv"

  def loadData(
                trainFile: String,
                testFile: String,
                sqlContext: SQLContext
              ): (DataFrame, DataFrame) = {
    val schemaArray = Array(
      StructField("Id", LongType),
      StructField("Dates", TimestampType),
      StructField("Category", StringType), // target variable
      StructField("Descript", StringType),
      StructField("DayOfWeek", StringType),
      StructField("PdDistrict", StringType),
      StructField("Resolution", StringType),
      StructField("Address", StringType),
      StructField("X", DoubleType),
      StructField("Y", DoubleType)
    )

    val trainSchema = StructType(schemaArray.filterNot(_.name == "Id"))
    val testSchema = StructType(schemaArray.filterNot { p =>
      Seq("Category", "Descript", "Resolution") contains p.name})

    val trainDF = sqlContext.read
      .format(csvFormat)
      .option("header", "true")
      .schema(trainSchema)
      .load(trainFile)

    val testDF = sqlContext.read
      .format(csvFormat)
      .option("header", "true")
      .schema(testSchema)
      .load(testFile)

    (trainDF, testDF)
  }

  // add a few time-related features to the original datasets such as:
  //   - the year
  //   - the month
  //   - the hour of day
  val enrichTime = (df: DataFrame) => {
    def dateUDF = udf { (timestamp: String) =>
      val timestampFormatter = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss")
      val dateFormat = DateTimeFormatter.ofPattern("YYYY-MM-dd")
      val time = timestampFormatter.parse(timestamp)
      dateFormat.format(time)
    }

    df
      .withColumn("HourOfDay", hour(col("Dates")))
      .withColumn("Month", month(col("Dates")))
      .withColumn("Year", year(col("Dates")))
      .withColumn("TimestampUTC", to_utc_timestamp(col("Dates"), "PST"))
      .withColumn("Date", dateUDF(col("TimestampUTC")))
  }

  // add a weekend feature telling whether or not the crime incident occurred on a weekend
  val enrichWeekend = (df: DataFrame) => {
    def weekendUDF = udf { (dayOfWeek: String) =>
      dayOfWeek match {
        case _ @ ("Friday" | "Saturday" | "Sunday") => 1
        case _ => 0
      }
    }
    df.withColumn("Weekend", weekendUDF(col("DayOfWeek")))
  }

  // add address-related features:
  //   - whether or not the crime incident occurred at an intersection
  //   - a street feature which is the street corresponding to the parsed address
  val enrichAddress = (df: DataFrame) => {
    def addressTypeUDF = udf { (address: String) =>
      if (address contains "/") "Intersection"
      else "Street"
    }

    val streetRegex = """\d{1,4} Block of (.+)""".r
    val intersectionRegex = """(.+) / (.+)""".r
    def addressUDF = udf { (address: String) =>
      streetRegex findFirstIn address match {
        case Some(streetRegex(s)) => s
        case None => intersectionRegex findFirstIn address match {
          case Some(intersectionRegex(s1, s2)) => if (s1 < s2) s1 else s2
          case None => address
        }
      }
    }
    df
      .withColumn("AddressType", addressTypeUDF(col("Address")))
      .withColumn("Street", addressUDF(col("Address")))
  }



  def main(args: Array[String]) {

    // Spark conf
    val conf = new SparkConf(true).setAppName("ML_classification").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    // Load data
    val trainFile = "~/Documents/projet/data/train.csv"
    val testFile = "~/Documents/projet/data/test.csv"
    val outputFile = "~/Documents/projet/data/output_classificationCrime"
    val (rawTrainDF, rawTestDF) = loadData(trainFile, testFile, sqlContext)

    // Enriching?
    val enrichFunctions = List(enrichTime, enrichWeekend, enrichAddress)
    val Array(enrichedTrainDF, enrichedTestDF) =
      Array(rawTrainDF, rawTestDF) map (enrichFunctions reduce (_ andThen _))

    // Pipeline
    val labelColName = "Category"
    val predictedLabelColName = "predictedLabel"
    val featuresColName = "Features"
    val numericFeatColNames = Seq("X", "Y")
    val categoricalFeatColNames = Seq(
      "DayOfWeek", "PdDistrict", "Weekend", "HourOfDay", "Month", "Year"
    )

    val allData = enrichedTrainDF
      .select((numericFeatColNames ++ categoricalFeatColNames).map(col): _*)
      .unionAll(enrichedTestDF
        .select((numericFeatColNames ++ categoricalFeatColNames).map(col): _*))
    allData.cache()







  }

  /*
  def loadData(
                trainFile: String,
                testFile: String,
                sqlContext: SQLContext
              ): (DataFrame, DataFrame) = {
    val schemaArray = Array(
      StructField("Id", LongType),
      StructField("Dates", TimestampType),
      StructField("Category", StringType), // target variable
      StructField("Descript", StringType),
      StructField("DayOfWeek", StringType),
      StructField("PdDistrict", StringType),
      StructField("Resolution", StringType),
      StructField("Address", StringType),
      StructField("X", DoubleType),
      StructField("Y", DoubleType)
    )

    val trainSchema = StructType(schemaArray.filterNot(_.name == "Id"))
    val testSchema = StructType(schemaArray.filterNot { p =>
      Seq("Category", "Descript", "Resolution") contains p.name
    })

    val trainDF = sqlContext.read
      .format(csvFormat)
      .option("header", "true")
      .schema(trainSchema)
      .load(trainFile)

    val testDF = sqlContext.read
      .format(csvFormat)
      .option("header", "true")
      .schema(testSchema)
      .load(testFile)

    (trainDF, testDF)
  }
  */

}
