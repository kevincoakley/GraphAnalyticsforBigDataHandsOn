Hands On: Building A Graph
===========================

## Setup the Datasets Used for this Example

Set log level to error, suppress info and warn messages

input:

    import org.apache.log4j.Logger
    import org.apache.log4j.Level
    
    Logger.getLogger("org").setLevel(Level.ERROR)
    Logger.getLogger("akka").setLevel(Level.ERROR)

Import the GraphX and RDD libraries.

input:

    import org.apache.spark.graphx._
    import org.apache.spark.rdd._

    import scala.io.Source
    import org.apache.spark.SparkContext

Print the first 5 lines of each tab delimited text file.

input:

    Source.fromFile("./EOADATA/facebook_combined.txt").getLines().take(5).foreach(println)

output:

    ﻿0 1
    0 2
    0 3
    0 4
    0 5
    
input:

    val facebookGraph = GraphLoader.edgeListFile(sc, "./EOADATA/facebook_combined.txt")

output:

    ﻿facebookGraph: org.apache.spark.graphx.Graph[Int,Int] = org.apache.spark.graphx.impl.GraphImpl@24e2ad06
    
Print the first 5 vertices and edges.

input:

    facebookGraph.vertices.take(5)
    facebookGraph.edges.take(5)

output:

    ﻿res4: Array[(org.apache.spark.graphx.VertexId, Int)] = Array((384,1), (1084,1), (3702,1), (3007,1), (667,1))
    ﻿res5: Array[org.apache.spark.graphx.Edge[Int]] = Array(Edge(0,1,1), Edge(0,2,1), Edge(0,3,1), Edge(0,4,1), Edge(0,5,1))

Find the VertexId(s) of all vertex that have an edge where the source VertexId is 1.

input:

    facebookGraph.edges.filter(_.srcId == 1).map(_.dstId).collect()
    
output:

    ﻿res6: Array[org.apache.spark.graphx.VertexId] = Array(48, 53, 54, 73, 88, 92, 119, 126, 133, 194, 236, 280, 299, 315, 322, 346)

Find the VertexId(s) of all vertex that have an edge where the destination VertexId is 2.

input:

    facebookGraph.edges.filter(_.dstId == 2).map(_.srcId).collect()
    
output:

    ﻿res7: Array[org.apache.spark.graphx.VertexId] = Array(0)
    
