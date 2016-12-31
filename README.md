#Apache Spark

##Prerequisities & Set-up
The material uses [vagrant](https://www.vagrantup.com/) for demonstration and hands-on exercises.

**Note**: Install Vagrant following this [link](https://www.vagrantup.com/docs/installation/) and also install virtualbox which shall be used as the virtual machine provider. Once installed we spin up a virtual machine to try our exercises.

To download and run the exercises clone the github repository with

    $ git clone https://github.com/somasundaramsekar/sparkquickbook.git

To bring the vm up execute the following commands



    $ cd sparkquickbook
    $ vagrant box add spark-vagrant.json
    $ vagrant up

To login to the vm

    $ vagrant ssh

To stop the vm

    $ vagrant halt

To destroy the vm

    $ vagrant destroy

**Note**: destroy will purge the virtual machine as a whole, this can be used, for instance to quickly destroy and bring vm back from the base state.

**Note**: additionally you can mount a local folder into the vm, that way use can use IDE to work with the Spark project, while packaging and running that from inside the vm.

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



RDD(Resilient Distributed Dataset) is at the core of Spark, abstracting the distributed computation, that gives it the ability to provide myraid of api and connectors to both upstream  clients and downstream datasources and streams.

<a href="http://imgur.com/YGGNKzR"><img src="http://i.imgur.com/YGGNKzR.jpg" title="source: imgur.com" /></a>

Let us take an example of the file that is stored in HDFS, HDFS by default splits the files into blocks of 128MB and saves it across datanodes for resiliency. RDD are the abstraction of this distribution, hiding away the complexity of storage, execution and fault tolerance from the clients.

Spark code when executed, creates a lineage graph(DAG). the scheduler, then schedules the execution of the the DAG in each of the nodes in which the data resides. This is similar to Hadoop Map-Reduce, where map tasks executes on local data and reduce tasks shuffles the data across nodes to produce results.

In Spark, the data is loaded into the main memory(RAM) and the individual tasks(stages) of the Job(An Spark program) is run. This provides the speed that distinguishes Spark from Hadoop Map-Reduce. The data(RDD) can be cached(in-memory or on disk) between stages and multiple tasks can be run on the same cached RDD.

With that context let us have a look at the generic wordcount example with Spark Scala api with detailed documentataion

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

**NOTE**: All the execution from the `sbt console`, or `spark-shell repl` or by executing the above class from an IDE are all executed within an JVM using the spark libraries. This is a quick way to get a taste of the Spark code you write(in most cases), but in most cases the same code can run without any change(except for few configurations, like master URL) in the cluster.

##The Spark RDD
We will dedicate this entire section to RDD, the core of Spark's execution engine, that the api's like DataFrame and DataSet user internally and which was atleast until 1.x was the primary programming interface for Spark.

RDD as explained previously is an abstraction of distributed collection of data, this abstraction helps hide the complexities of Distribution of data, aka partitions and provides a uniform functional collection like interface to work with. RDD is immutable, that is it any transformation when executed, does not mutate the data, instead returns new RDD with the transformation applied.

    scala> val file = sc.textFile("README.md")
    file: org.apache.spark.rdd.RDD[String] = README.md MapPartitionsRDD[3] at textFile at <console>:22

    scala> val fileAfterSplit = file.flatMap(line => line.split(" "))
    fileAfterSplit: org.apache.spark.rdd.RDD[String] = MapPartitionsRDD[5] at flatMap at <console>:23

it is evident from the above 2 lines of code that two different instance of `RDD[String]` is created. one per each transformation.

Users can create an RDD by loading an external dataset like from a local file, files from HDFS or Amazon S3, but also from an local collection of object, but you dont want to do that in production though, that we cannot either load terabytes of objects into the clients memory or distribute them over network. That said, this is how we do it, with the `parallelize()` method of SparkContext

    scala> val lines = sc.parallelize(List("A", "Sample", "in memory", "object", "collection"))
    lines: org.apache.spark.rdd.RDD[String] = ParallelCollectionRDD[9] at parallelize at <console>:22

###Operations
RDD allows two types of operations on dataset, *transformations and actions*.

If an operation on a method return an RDD it is an transformation, otherwise the method is an action. Transformations are fundamentally very different from actions, that transformation are not executed until the action action is called on an RDD.

Actions either returns an result back to the client or perform an side-effect such as saving the result to an hdfs file or caching the intermediate RDD etc.,

Let us try to understand them with an example, fire up with `spark-shell` with `sbt console` from `scalaquickbook/` folder, from within the vagrant vm.

**NOTE**: The cloned github project has an `supportingfiles/` folder with the supporting files of this material

Let us analyze a sample apache web server log file with Spark

1. Load the File

    scala> val logFile = sc.textFile("supportingfiles/access_log")
    16/12/31 14:00:54 INFO SparkContext: Created broadcast 1 from textFile at <console>:22
    logFile: org.apache.spark.rdd.RDD[String] = supportingfiles/access_log MapPartitionsRDD[3] at textFile at <console>:22

2. Split each line with empty space as delimiter

    scala> val splitLines = logFile.map(line => line.split("""\s+"""))
    splitLines: org.apache.spark.rdd.RDD[Array[String]] = MapPartitionsRDD[6] at map at <console>:23

we see that type of RDD is now transformed from RDD[String] to RDD[Array[String]], as of this point no processing has occured, except for loading the file into memory, since the file is a local file, in which case the `textFile()` will load the file into memory.

3. To get a taste of what has happened to the file, let us trigger an action

    scala> splitLines.first

on this, you see spark spinning up jobs and you will notice that file is actually getting processed after which you will receive the result of your action `first()`, which is to get the first element of the collection the `Array[String]`

    res3: Array[String] = Array(64.242.88.10, -, -, [07/Mar/2004:16:05:49, -0800], "GET, /twiki/bin/edit/Main/Double_bounce_sender?topicparent=Main.ConfigurationVariables, HTTP/1.1", 401, 12846)



##References

 1. https://www.safaribooksonline.com/library/view/learning-spark
 2. https://www.safaribooksonline.com/library/view/spark-in-action
 3. https://www.safaribooksonline.com/library/view/advanced-analytics-with/
 4.


