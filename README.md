# sparkquickbook
A quick book on Spark with Scala
https://hadoop.apache.org/docs/stable/hadoop-project-dist/hadoop-common/SingleCluster.html
http://spark.apache.org/downloads.html

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

Spark also comes built-in with a Scala [REPL](https://en.wikipedia.org/wiki/Read%E2%80%93eval%E2%80%93print_loop) for quick prototyping or exploring the APIs calles spark-shell

Let us have a look at the following wordcount example with the scala api

1. Fire up the `spark-shell`
2. Execute the below lines

```scala
val spark = SparkSession.builder().appName("Spark wordcount")
val file = spark.sparkContext.textFile("path/to/any/text/file.extension")
val counts = file.flatMap(line => line.split(" "))
    .map(word => (word, 1)).countByKey()
```







##References

 1. https://www.safaribooksonline.com/library/view/learning-spark
 2. https://www.safaribooksonline.com/library/view/spark-in-action
 3. https://www.safaribooksonline.com/library/view/advanced-analytics-with/
 4.  



