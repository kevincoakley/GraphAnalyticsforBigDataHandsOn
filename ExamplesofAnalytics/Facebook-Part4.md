Hands On: Network Connectedness and Clustering Components
=========================================================

input:
    
    import org.graphstream.graph.implementations._

input:

    val graph: SingleGraph = new SingleGraph("facebookGraph")

output:

    ﻿graph: org.graphstream.graph.implementations.SingleGraph = facebookGraph

Set up the visual attributes for graph visualization.

input:

    graph.addAttribute("ui.stylesheet","url(file:.//style/stylesheet-simple)")
    graph.addAttribute("ui.quality")
    graph.addAttribute("ui.antialias")

Given the facebookGraph, load the graphX vertices into GraphStream

input:

    for ((id, _) <- facebookGraph.vertices.collect()) {
      graph.addNode(id.toString).asInstanceOf[SingleNode]
    }

Load the graphX edges into GraphStream edges

input:

    for ((Edge(x, y, _), count) <- facebookGraph.edges.collect().zipWithIndex) {
      graph.addEdge(count.toString, x.toString, y.toString).asInstanceOf[AbstractEdge]
    }

Display the graph.

input:

    graph.display()
    
output:

    INSERT GRAPH HERE
    
