import org.apache.spark._

object sparkStereoPrix2 {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("sparkStereoPrix2").setMaster("local[2]")
    val spark = new SparkContext(conf)
    val inputFile = spark.textFile(args(0))

    var catProdFreq = inputFile.map(x => ((x.split(" ").toList(5).toString(), x.split(" ").toList(4).toString()), 1)).reduceByKey(_ + _)
    var catProdFreq2 = catProdFreq map { case ((cat, prod), freq) => (cat -> (prod, freq)) }
    var catListProdFreq = catProdFreq2.groupByKey().map(c => (c._1, c._2.toList.maxBy(_._2))) map { case (cat, (prod, freq)) => (cat, prod) }
    catListProdFreq.sortByKey().foreach(println(_))
    spark.stop()
  }
}