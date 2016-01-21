//
// Set log level to error, suppress info and warn messages
//
import org.apache.log4j.Logger
import org.apache.log4j.Level

Logger.getLogger("org").setLevel(Level.ERROR)
Logger.getLogger("akka").setLevel(Level.ERROR)


//
// Hands On: Joining Graph Datasets
//
import org.apache.spark.graphx._
import org.apache.spark.rdd._

import org.apache.spark.SparkContext

// Do not include the next line in the Hands On, the spark-shell command will set this during
// initialization, only here because IntelliJ complains that it is missing.
val sc: SparkContext

val airports: RDD[(VertexId, String)] = sc.parallelize(
    List((1L, "Los Angeles International Airport"),
      (2L, "Narita International Airport"),
      (3L, "Singapore Changi Airport"),
      (4L, "Charles de Gaulle Airport"),
      (5L, "Toronto Pearson International Airport")))

val flights: RDD[Edge[String]] = sc.parallelize(
  List(Edge(1L,4L,"AA1123"),
    Edge(2L, 4L, "JL5427"),
    Edge(3L, 5L, "SQ9338"),
    Edge(1L, 5L, "AA6653"),
    Edge(3L, 4L, "SQ4521")))

val flightGraph = Graph(airports, flights)


flightGraph.triplets.foreach(t => println("Departs from: " + t.srcAttr + " Arrives at: " + t.dstAttr + " Flight Number: " + t.attr))


flightGraph.vertices.foreach(println)


case class AirportInformation(city: String, code: String)

val airportInformation: RDD[(VertexId, AirportInformation)] = sc.parallelize(
  List((2L, AirportInformation("Tokyo", "NRT")),
    (3L, AirportInformation("Singapore", "SIN")),
    (4L, AirportInformation("Paris", "CDG")),
    (5L, AirportInformation("Toronto", "YYZ")),
    (6L, AirportInformation("London", "LHR")),
    (7L, AirportInformation("Hong Kong", "HKG"))))


def appendAirportInformation(id: VertexId, name: String, airportInformation: AirportInformation): String = name + ":"+ airportInformation.city
val flightJoinedGraph =  flightGraph.joinVertices(airportInformation)(appendAirportInformation)
flightJoinedGraph.vertices.foreach(println)


val flightOuterJoinedGraph = flightGraph.outerJoinVertices(airportInformation)((_,name, airportInformation) => (name, airportInformation))
flightOuterJoinedGraph.vertices.foreach(println)


val flightOuterJoinedGraphTwo = flightGraph.outerJoinVertices(airportInformation)((_, name, airportInformation) => (name, airportInformation.getOrElse(AirportInformation("NA","NA"))))
flightOuterJoinedGraphTwo.vertices.foreach(println)


case class Airport(name: String, city: String, code: String)

val flightOuterJoinedGraphThree = flightGraph.outerJoinVertices(airportInformation)((_, name, b) => b match {
  case Some(airportInformation) => Airport(name, airportInformation.city, airportInformation.code)
  case None => Airport(name, "", "")
})
flightOuterJoinedGraphThree.vertices.foreach(println)


val flightAirport = flightJoinedGraph.vertices
flightAirport.foreach(println)

flightAirport.mapValues(s => s.split(':')(0)).foreach(println)

flightAirport.mapValues((vid,s) => s.split(':')(0)).foreach(println)


val flightsv = flightGraph.vertices
flightsv.innerJoin(airportInformation)((vid, name, b) => name + " is in " + b.city).foreach(println)

flightsv.leftJoin(airportInformation)((vid, name, b) => b match {
  case Some(airportInformation) => name + " is in " + airportInformation.city
  case None => name + "is in an unknown city"
}).foreach(println)


val flightse = flightGraph.edges
flightse.foreach(println)

val bidirectedGraph = Graph(airports, flightse union flightse.reverse)

bidirectedGraph.edges.foreach(println)

