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
import org.apache.spark.rdd._

import scala.io.Source
import org.apache.spark.SparkContext

// Do not include the next line in the Hands On, the spark-shell command will set this during
// initialization, only here because IntelliJ complains that it is missing.
val sc: SparkContext

// Print the first 5 lines of each comma delimited text file.
Source.fromFile("./data/metro.csv").getLines().take(5).foreach(println)
Source.fromFile("./data/country.csv").getLines().take(5).foreach(println)
Source.fromFile("./data/metro_country.csv").getLines().take(5).foreach(println)

// Create case classes for the places (metros and countries).
class PlaceNode(val name: String) extends Serializable
case class Metro(override val name: String, population: Int) extends PlaceNode(name)
case class Country(override val name: String) extends PlaceNode(name)

// Read the comma delimited text file metros.csv into an RDD of Metro vertices, ignore lines
// that start with # and map the columns to: id, Metro(name, population).
val metros: RDD[(VertexId, PlaceNode)] =
  sc.textFile("./data/metro.csv").
    filter(! _.startsWith("#")).
    map {line =>
      val row = line split ','
      (0L + row(0).toInt, Metro(row(1), row(2).toInt))
    }

// Read the comma delimited text file country.csv into an RDD of Country vertices, ignore lines
// that start with # and map the columns to: id, Country(name). Add 100 to the country indexes
// so they are unique from the metro indexes.
val countries: RDD[(VertexId, PlaceNode)] =
  sc.textFile("./data/country.csv").
    filter(! _.startsWith("#")).
    map {line =>
      val row = line split ','
      (100L + row(0).toInt, Country(row(1)))
    }

// Read the comma delimited text file metro_country.tsv into an RDD[Edge[Int]] collection. Remember
// to add 100 to the index of the country.
val mclinks: RDD[Edge[Int]] =
  sc.textFile("./data/metro_country.csv").
    filter(! _.startsWith("#")).
    map {line =>
      val row = line split ','
      Edge(0L + row(0).toInt, 100L + row(1).toInt, 1)
    }

// Concatenate the two sets of nodes into a single RDD.
val nodes = metros ++ countries

// Pass the concatenated RDD to the Graph() factory method along with the RDD link
val metrosGraph = Graph(nodes, mclinks)

// Print the first 5 vertices and edges.
metrosGraph.vertices.take(5)
metrosGraph.edges.take(5)

// Find the VertexId(s) of all vertex that have an edge where the source VertexId is 1.
metrosGraph.edges.filter(_.srcId == 1).map(_.dstId).collect()

// Find the VertexId(s) of all vertex that have an edge where the destination VertexId is 103.
metrosGraph.edges.filter(_.dstId == 103).map(_.srcId).collect()

// Create a helper function that returns a description of the metro to country relationship.
def showTriplet(t: EdgeTriplet[PlaceNode, Int]): String =
  "The metropolitan area of " ++ t.srcAttr.name ++ " is in the country of " ++ t.dstAttr.name

// Use the showTriplet function to describe the relationship between the metros and the countries
// text form.
metrosGraph.triplets.take(5).foreach(showTriplet _ andThen println _)



//
// Hands On: Building a Degree Histogram
//

// Print the number of links.
metrosGraph.numEdges

// Print the number of nodes.
metrosGraph.numVertices

// Define a min and max function.
def max(a: (VertexId, Int), b: (VertexId, Int)): (VertexId, Int) = {
  if (a._2 > b._2) a else b
}

def min(a: (VertexId, Int), b: (VertexId, Int)): (VertexId, Int) = {
  if (a._2 <= b._2) a else b
}

// Find which which VertexId and the edge count of the vertex with the most out edges. (This
// can be any vertex because all vertices have one out edge.)
metrosGraph.outDegrees.reduce(max)
// Print the returned vertex.
metrosGraph.vertices.filter(_._1 == 5).collect()

// Find which which VertexId and the edge count of the vertex with the most in edges.
metrosGraph.inDegrees.reduce(max)
// Print the returned vertex.
metrosGraph.vertices.filter(_._1 == 108).collect()

// Find the number vertexes that have only one out edge.
metrosGraph.outDegrees.filter(_._2 <= 1).count

// Find the maximum and minimum degrees of the connections in the network.
metrosGraph.degrees.reduce(max)
metrosGraph.degrees.reduce(min)

// Print the histogram data of the degrees for countries only.
metrosGraph.degrees.
  filter { case (vid, count) => vid >= 100 }. // Apply filter so only VertexId < 100 (countries) are included
  map(t => (t._2,t._1)).
  groupByKey.map(t => (t._1,t._2.size)).
  sortBy(_._1).collect()



//
// Hands On: Plot the Degree Histogram
//

import breeze.linalg._
import breeze.plot._

// Define a function to create a histogram of the degrees. See metrosGraph.degrees... from above.
// Only include countries!
def degreeHistogram(net: Graph[PlaceNode, Int]): Array[(Int, Int)] =
  net.degrees.
    filter { case (vid, count) => vid >= 100 }.
    map(t => (t._2,t._1)).
    groupByKey.map(t => (t._1,t._2.size)).
    sortBy(_._1).collect()


// Get the probability distribution (degree distribution) from the degree histogram by normalizing
// the node degrees by the total number of nodes, so that the degree probabilities add up to one.
val nn = metrosGraph.vertices.filter{ case (vid, count) => vid >= 100 }.count()

val metroDegreeDistribution = degreeHistogram(metrosGraph).map({case(d,n) => (d,n.toDouble/nn)})

// Plot degree distribution and the histogram of node degrees.
val f = Figure()
val p1 = f.subplot(2,1,0)
val x = new DenseVector(metroDegreeDistribution map (_._1.toDouble))
val y = new DenseVector(metroDegreeDistribution map (_._2))

p1.xlabel = "Degrees"
p1.ylabel = "Distribution"
p1 += plot(x, y)
p1.title = "Degree distribution"


val p2 = f.subplot(2,1,1)
val metrosDegrees = metrosGraph.degrees.filter { case (vid, count) => vid >= 100 }.map(_._2).collect()

p2.xlabel = "Degrees"
p2.ylabel = "Histogram of node degrees"
p2 += hist(metrosDegrees, 20)



//
// Hands On: Network Connectedness and Clustering Components
//

// To make the graph more interesting, create a new graph and add the continents.
Source.fromFile("./data/continent.csv").getLines().take(5).foreach(println)
Source.fromFile("./data/country_continent.csv").getLines().take(5).foreach(println)

case class Continent(override val name: String) extends PlaceNode(name)

val continents: RDD[(VertexId, PlaceNode)] =
  sc.textFile("./data/continent.csv").
    filter(! _.startsWith("#")).
    map {line =>
      val row = line split ','
      (200L + row(0).toInt, Continent(row(1))) // Add 200 to the VertexId to keep the indexes unique
    }

val cclinks: RDD[Edge[Int]] =
  sc.textFile("./data/country_continent.csv").
    filter(! _.startsWith("#")).
    map {line =>
      val row = line split ','
      Edge(100L + row(0).toInt, 200L + row(1).toInt, 1)
    }

// Concatenate the three sets of nodes into a single RDD.
val cnodes = metros ++ countries ++ continents

// Concatenate the two sets of edges
val clinks = mclinks ++ cclinks

val countriesGraph = Graph(cnodes, clinks)

import org.graphstream.graph.implementations._

val graph: SingleGraph = new SingleGraph("countriesGraph")

// Set up the visual attributes for graph visualization.
graph.addAttribute("ui.stylesheet","url(file:.//style/stylesheet)")
graph.addAttribute("ui.quality")
graph.addAttribute("ui.antialias")

// Load the graphX vertices into GraphStream nodes.
for ((id:VertexId, place:PlaceNode) <- countriesGraph.vertices.collect())
{
  val node = graph.addNode(id.toString).asInstanceOf[SingleNode]
  node.addAttribute("name", place.name)
  node.addAttribute("ui.label", place.name)

  if (place.isInstanceOf[Metro])
    node.addAttribute("ui.class", "metro")
  else if(place.isInstanceOf[Country])
    node.addAttribute("ui.class", "country")
  else if(place.isInstanceOf[Continent])
    node.addAttribute("ui.class", "continent")
}

// Load the graphX edges into GraphStream edges.
for (Edge(x,y,_) <- countriesGraph.edges.collect()) {
  graph.addEdge(x.toString ++ y.toString, x.toString, y.toString, true).asInstanceOf[AbstractEdge]
}

// Display the graph.
// metros: small blue dots.
// countries: medium red dots.
// continents: large green dots.
graph.display()


//
// Single Source Shortest Path (All are Infinity)
//

val sourceId: VertexId = 0 // The ultimate source

// Initialize the graph such that all vertices except the root have distance infinity.
val initialGraph : Graph[(Double, List[VertexId]), Int] = countriesGraph.mapVertices((id, _) => if (id == sourceId) (0.0, List[VertexId](sourceId)) else (Double.PositiveInfinity, List[VertexId]()))

val singleSourceShortestPath = initialGraph.pregel((Double.PositiveInfinity, List[VertexId]()), Int.MaxValue, EdgeDirection.Out)(
  // Vertex Program
  (id, dist, newDist) => if (dist._1 < newDist._1) dist else newDist,

  // Send Message
  triplet => {
    if (triplet.srcAttr._1 < triplet.dstAttr._1 - triplet.attr ) {
      Iterator((triplet.dstId, (triplet.srcAttr._1 + triplet.attr , triplet.srcAttr._2 :+ triplet.dstId)))
    } else {
      Iterator.empty
    }
  },

  //Merge Message
  (a, b) => if (a._1 < b._1) a else b)
println(singleSourceShortestPath.vertices.collect.mkString("\n"))


//
// Connected Components
//

val cc = countriesGraph.connectedComponents()

// Find the number of connected components (7, each continent).
cc.vertices.map(_._2).collect.distinct.length

// Find the lowest VertexId in each component.
cc.vertices.map(_._2).distinct.collect

// Find the lowest VertexId in each component and the number of vertices in each component.
cc.vertices.groupBy(_._2).map(p => (p._1,p._2.size)).sortBy(x => x._2).collect()

// Find the largest component and print the number of vertices in that component.
def largestComponent(cc: Graph[VertexId, Int]): (VertexId, Int) =
  cc.vertices.map(x => (x._2,x._1)).
    groupBy(_._1).
    map(p => (p._1,p._2.size)).
    max()(Ordering.by(_._2))

largestComponent(cc)


//
// Community detection through label propagation (Label Propagation Algorithm)
//

// Initalize the graph by setting the label of each vertex to its identifier.
val lpaGraph = countriesGraph.mapVertices { case (vid, _) => vid }

// Start with an empty map then associate a community label to the number of neighbors that have the same label.
val initialMessage = Map[VertexId, Long]()
val maxSteps = 50

// Using the Pregel programming model, each node will inform its neighbors of its current label using the sendMsg function.
// The source node will receive the destination node's label, and vice versa for each Triplet.
def sendMessage(e: EdgeTriplet[VertexId, Int]): Iterator[(VertexId, Map[VertexId, Long])] = {
  Iterator((e.srcId, Map(e.dstAttr -> 1L)), (e.dstId, Map(e.srcAttr -> 1L)))
}

// A node determines its community label as the one to which the majority of its neighbors currently belong to.
def vertexProgram(vid: VertexId, attr: Long, message: Map[VertexId, Long]): VertexId = {
  if (message.isEmpty) attr else message.maxBy(_._2)._1
}

// Combine all the messages received by a node from its neighbors into a single map.
// Sum the corresponding number of neighbors if both the messages contain the same label.
def mergeMessage(count1: Map[VertexId, Long], count2: Map[VertexId, Long])
: Map[VertexId, Long] = {
  (count1.keySet ++ count2.keySet).map { i =>
    val count1Val = count1.getOrElse(i, 0L)
    val count2Val = count2.getOrElse(i, 0L)
    i -> (count1Val + count2Val)
  }.toMap
}

// Run the LPA algorithm by calling the Pregel method.
Pregel(lpaGraph, initialMessage, maxIterations = maxSteps)(
  vprog = vertexProgram,
  sendMsg = sendMessage,
  mergeMsg = mergeMessage)


//
// Page Rank
//

// Run PageRank with an error tolerance of 0.0001
val ranks = countriesGraph.pageRank(0.001).vertices
// Find the top 10 Places (metros, countries or continents).
val top10Places = ranks.sortBy(_._2, false).take(10)
