//
// Set log level to error, suppress info and warn messages
//
import org.apache.log4j.Logger
import org.apache.log4j.Level

Logger.getLogger("org").setLevel(Level.ERROR)
Logger.getLogger("akka").setLevel(Level.ERROR)


//
// Hands On: Building A Graph
//
import org.apache.spark.graphx._
//import org.apache.spark.rdd._

import scala.io.Source
import org.apache.spark.SparkContext

// Do not include the next line in the Hands On, the spark-shell command will set this during
// initialization, only here because IntelliJ complains that it is missing.
val sc: SparkContext

// Print the first 5 lines of each tab delimited text file.
Source.fromFile("./data/com-dblp.ungraph.txt").getLines().take(5).foreach(println)

val FacebookGraph = GraphLoader.edgeListFile(sc, "./data/facebook_combined.txt")

// Print the first 5 vertices and edges.
FacebookGraph.vertices.take(5)
FacebookGraph.edges.take(5)

// Find the VertexId(s) of all vertex that have an edge where the source VertexId is 1.
FacebookGraph.edges.filter(_.srcId == 1).map(_.dstId).collect()

// Find the VertexId(s) of all vertex that have an edge where the destination VertexId is 2.
FacebookGraph.edges.filter(_.dstId == 2).map(_.srcId).collect()



//
// Hands On: Building a Degree Histogram
//

// Print the number of links.
FacebookGraph.numEdges

// Print the number of nodes.
FacebookGraph.numVertices

// Define a min and max function.
def max(a: (VertexId, Int), b: (VertexId, Int)): (VertexId, Int) = {
  if (a._2 > b._2) a else b
}

def min(a: (VertexId, Int), b: (VertexId, Int)): (VertexId, Int) = {
  if (a._2 <= b._2) a else b
}

// Find which which VertexId and the edge count of the vertex with the most out edges. (This
// can be any vertex because all vertices have one out edge.)
FacebookGraph.outDegrees.reduce(max)

// Find which which VertexId and the edge count of the vertex with the most in edges.
FacebookGraph.inDegrees.reduce(max)

// Find the number vertexes that have only one out edge.
FacebookGraph.outDegrees.filter(_._2 <= 1).count

// Find the maximum and minimum degrees of the connections in the network.
FacebookGraph.degrees.reduce(max)
FacebookGraph.degrees.reduce(min)

// Print the histogram data of the degrees.
FacebookGraph.degrees.
  map(t => (t._2,t._1)).
  groupByKey.map(t => (t._1,t._2.size)).
  sortBy(_._1).collect()



//
// Hands On: Plot the Degree Histogram
//

import breeze.linalg._
import breeze.plot._

// Define a function to create a histogram of the degrees. See FacebookGraph.degrees... from above.
// Only include countries!
def degreeHistogram(net: Graph[Int, Int]): Array[(Int, Int)] =
  net.degrees.
    map(t => (t._2,t._1)).
    groupByKey.map(t => (t._1,t._2.size)).
    sortBy(_._1).collect()


// Get the probability distribution (degree distribution) from the degree histogram by normalizing
// the node degrees by the total number of nodes, so that the degree probabilities add up to one.
val nn = FacebookGraph.numVertices
val FacebookGraphDistribution = degreeHistogram(FacebookGraph).map({case(d,n) => (d,n.toDouble/nn)})

// Plot degree distribution and the histogram of node degrees.
val f = Figure()
val p1 = f.subplot(2,1,0)
val x = new DenseVector(FacebookGraphDistribution map (_._1.toDouble))
val y = new DenseVector(FacebookGraphDistribution map (_._2))

p1.xlabel = "Degrees"
p1.ylabel = "Distribution"
p1 += plot(x, y)
p1.title = "Degree distribution"


val p2 = f.subplot(2,1,1)
val FacebookGraphDegrees = FacebookGraph.degrees.map(_._2).collect()

p2.xlabel = "Degrees"
p2.ylabel = "Histogram of node degrees"
p2 += hist(FacebookGraphDegrees, 1000)



//
// Hands On: Network Connectedness and Clustering Components
//

import org.graphstream.graph.implementations._

val graph: SingleGraph = new SingleGraph("FacebookGraph")

// Set up the visual attributes for graph visualization.
graph.addAttribute("ui.stylesheet","url(file:.//style/stylesheet-simple)")
graph.addAttribute("ui.quality")
graph.addAttribute("ui.antialias")

// Given the coAuthorshipGraph, load the graphX vertices into GraphStream
for ((id, _) <- FacebookGraph.vertices.collect()) {
  graph.addNode(id.toString).asInstanceOf[SingleNode]
}

// Load the graphX edges into GraphStream edges
//for (Edge(x, y, _) <- coAuthorshipGraph.edges.collect()) {
for ((Edge(x, y, _), count) <- FacebookGraph.edges.collect().zipWithIndex) {
  graph.addEdge(count.toString, x.toString, y.toString).asInstanceOf[AbstractEdge]
}

// Display the graph.
graph.display()
