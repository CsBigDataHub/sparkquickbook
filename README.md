# sparkquickbook
A quick book on Spark with Scala
https://hadoop.apache.org/docs/stable/hadoop-project-dist/hadoop-common/SingleCluster.html
http://spark.apache.org/downloads.html

#Apache Spark

##Introduction
Apache Spark is a cluster computing platform designed to be fast and general-purpose.

On the speed side, Spark extends the popular MapReduce model to efficiently support more types of computations, including interactive queries and stream processing.  One of the main features Spark offers for speed is the ability to run computations in memory, but the system is also more efficient than MapReduce for complex applications running on disk. Due to its in-memory execution model, spark has the ability to speed up calculations north of 100 times in some cases, compared to Map-Reduce.

Spark really shines where iterative processing is required on the datasets, like in the case of calculating shortest path  [Ref](http://www.cse.psu.edu/~huv101/papers/sbgv_2007_icpads.pdf)

<a href="http://imgur.com/NCO1fzI"><img src="http://i.imgur.com/NCO1fzI.png" title="source: imgur.com" /></a>
*Spark Stack*

<a href="http://imgur.com/w9m5fSa"><img src="http://i.imgur.com/w9m5fSa.jpg" title="source: imgur.com" /></a>
*Spark Components and Interactions*

**Consiceness**
Spark is written in scala and provides programming api for Scala, Java, Python and more recently R, with a bit of performance difference between each of them, Scala is the default, concise and has benchmarked performance while, Java lacks few features compared to Scala, Python when used only with RDD api takes a severe performance hit, which becomes negligible with Dataframes and Datasets.

##Getting the feet wet

**NOTE**: Spark also comes built-in with a Scala [REPL](https://en.wikipedia.org/wiki/Read%E2%80%93eval%E2%80%93print_loop) for quick prototyping or exploring the APIs calles spark-shell



RDD(Resilient Distributed Dataset) is at the core of the Spark abstracting the distributed computation, that gives it the ability to provide myraid of api and connectors to both upstream  clients and downstream datasources and streams.

<a href="http://imgur.com/YGGNKzR"><img src="http://i.imgur.com/YGGNKzR.jpg" title="source: imgur.com" /></a>

Let us take an example of the file that is stored in HDFS, HDSF by default splits the files into blocks of 128MB and saves it across datanodes for resiliency. RDD are the abstraction of this distribution, hiding away the complexity of storage, execution and fault tolerance from the clients.

Spark code when executed, creates a lineage graph(DAG). the scheduler, then schedules the execution of the the DAG in each of the nodes in which the data resides. This is similar to Hadoop Map-Reduce, where map tasks executes on local data and reduce tasks shuffles the data across nodes to produce results.

In Spark, the data is loaded into the main memory(RAM) and the individual tasks(stages) of the Job(An Spark program) is run. This provides the speed that distinguishes Spark from Hadoop Map-Reduce. The data(RDD) can be cached(in-memory or on disk) between stages and multiple tasks can be run on the same cached RDD.

With that context let us have a look at the generic wordcount example with Spark Scala api

```scala
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
```






##References

 1. https://www.safaribooksonline.com/library/view/learning-spark
 2. https://www.safaribooksonline.com/library/view/spark-in-action
 3. https://www.safaribooksonline.com/library/view/advanced-analytics-with/
 4.

