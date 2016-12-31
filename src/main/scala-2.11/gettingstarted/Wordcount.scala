package gettingstarted

import org.apache.spark.sql.SparkSession

object Wordcount extends App {

  /*
  Create a SparkSession instance with the builder, with a master()
  that dictates it to use local master for our local testing
  purpose with [2] threads
   */
  val spark = SparkSession.builder().appName("Spark wordcount").master("local[2]").getOrCreate()
  /*
  Specify the text file README.md with the textFile method of the SparkContext
   */
  val file = spark.sparkContext.textFile("README.md")
  /*
  Spark Scala api tows inline with the scala collections api
  thus enabling the programmers use a unified programming models
  irrespective of the abstraction.

  Apart from the regular collection functions, Spark also provides
  RDD specific methods like countByKey()
   */
  val counts = file.flatMap(line => line.split(" "))
    .map(word => (word, 1)).countByKey()

  /*
  A Spark program and inturn the DAG created from it, is divided into two categories
  Transformation: Code that specifies how the collection is to be manipulated
  Actions: The actual execution trigerring methids
  in this example collect() is the only action method, which triggers the execution of RDD DAG
   */
  counts.collect{
    case (word, count) => println(s"$word $count")
  }
}
