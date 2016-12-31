package gettingstarted

import org.apache.spark.sql.SparkSession

import scala.collection.Map

/**
  * Created by somasundar.sekar on 12/31/2016.
  */
object Wordcount extends App {

  /*
  Create a spark session with the SparkSession
   */
  val spark = SparkSession.builder().appName("Spark wordcount").master("local[2]").getOrCreate()
  val file = spark.sparkContext.textFile("README.md")
  val counts: Map[String, Long] = file.flatMap(line => line.split(" "))
    .map(word => (word, 1)).countByKey()
  counts.collect{
    case (word, count) => println(s"$word $count")
  }
}
