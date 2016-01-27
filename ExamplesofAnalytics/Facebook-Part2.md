Hands On: Building A Degree Histogram
=====================================

Print the number of links.

input:

    facebookGraph.numEdges

output:

    ﻿res8: Long = 88234
    
Print the number of nodes.

input:
    
    facebookGraph.numVertices

output:

    ﻿res9: Long = 4039
    
Define a min and max function.

input:

    def max(a: (VertexId, Int), b: (VertexId, Int)): (VertexId, Int) = {
      if (a._2 > b._2) a else b
    }
    
output:

    ﻿max: (a: (org.apache.spark.graphx.VertexId, Int), b: (org.apache.spark.graphx.VertexId, Int))(org.apache.spark.graphx.VertexId, Int)

input:
    
    def min(a: (VertexId, Int), b: (VertexId, Int)): (VertexId, Int) = {
      if (a._2 <= b._2) a else b
    }

output:

    ﻿min: (a: (org.apache.spark.graphx.VertexId, Int), b: (org.apache.spark.graphx.VertexId, Int))(org.apache.spark.graphx.VertexId, Int)
    
Find which which VertexId and the edge count of the vertex with the most out edges. (This
can be any vertex because all vertices have one out edge.)

input:

    facebookGraph.outDegrees.reduce(max)

output:

    ﻿res10: (org.apache.spark.graphx.VertexId, Int) = (107,1043)

Find which which VertexId and the edge count of the vertex with the most in edges.

input:

    facebookGraph.inDegrees.reduce(max)

output:

    ﻿res11: (org.apache.spark.graphx.VertexId, Int) = (1888,251)
    
Find the number vertexes that have only one out edge.

input:

    facebookGraph.outDegrees.filter(_._2 <= 1).count

output:

    ﻿res12: Long = 323
    
Find the maximum and minimum degrees of the connections in the network.

input:

    facebookGraph.degrees.reduce(max)
    
output:

    ﻿res13: (org.apache.spark.graphx.VertexId, Int) = (107,1045)
    
input:

    facebookGraph.degrees.reduce(min)

output:

    ﻿res14: (org.apache.spark.graphx.VertexId, Int) = (209,1)
    
Print the histogram data of the degrees.

input:

    facebookGraph.degrees.
      map(t => (t._2,t._1)).
      groupByKey.map(t => (t._1,t._2.size)).
      sortBy(_._1).collect()
      
output:

    ﻿res15: Array[(Int, Int)] = Array((1,75), (2,98), (3,93), (4,99), (5,93), (6,98), (7,98), (8,111), (9,100), (10,95), 
    (11,81), (12,82), (13,79), (14,87), (15,106), (16,82), (17,76), (18,73), (19,72), (20,63), (21,52), (22,63), (23,53), 
    (24,60), (25,55), (26,56), (27,49), (28,37), (29,38), (30,40), (31,38), (32,44), (33,35), (34,43), (35,36), (36,43), 
    (37,43), (38,44), (39,29), (40,27), (41,29), (42,21), (43,29), (44,21), (45,19), (46,24), (47,24), (48,24), (49,33), 
    (50,25), (51,20), (52,19), (53,15), (54,23), (55,23), (56,18), (57,23), (58,15), (59,11), (60,18), (61,18), (62,16), 
    (63,23), (64,13), (65,20), (66,22), (67,13), (68,16), (69,14), (70,17), (71,18), (72,15), (73,10), (74,10), (75,8), 
    (76,15), (77,10), (78,11), (79,16), (80,8), (81,4), (82,12), (83,17), (84,12), (85,10), (86,9), ...
    

