Hands On: Plot The Degree Histogram
====================================

input:

    import breeze.linalg._
    import breeze.plot._

Define a function to create a histogram of the degrees. See facebookGraph.degrees... from above.
Only include countries!

input:

    def degreeHistogram(net: Graph[Int, Int]): Array[(Int, Int)] =
      net.degrees.
        map(t => (t._2,t._1)).
        groupByKey.map(t => (t._1,t._2.size)).
        sortBy(_._1).collect()

output:

    ﻿degreeHistogram: (net: org.apache.spark.graphx.Graph[Int,Int])Array[(Int, Int)]

Get the probability distribution (degree distribution) from the degree histogram by normalizing
the node degrees by the total number of nodes, so that the degree probabilities add up to one.

input:

    val nn = facebookGraph.numVertices
    
output:

    ﻿nn: Long = 4039
    
input:

    val facebookGraphDistribution = degreeHistogram(facebookGraph).map({case(d,n) => (d,n.toDouble/nn)})

output:

    ﻿facebookGraphDistribution: Array[(Int, Double)] = Array((1,0.018568952711067097), (2,0.024263431542461005), 
    (3,0.0230255013617232), (4,0.024511017578608567), (5,0.0230255013617232), (6,0.024263431542461005), (7,0.024263431542461005), 
    (8,0.027482050012379303), (9,0.02475860361475613), (10,0.02352067343401832), (11,0.020054468927952464), 
    (12,0.020302054964100025), (13,0.01955929685565734), (14,0.02153998514483783), (15,0.026244119831641495), (16,0.020302054964100025), 
    (17,0.018816538747214655), (18,0.018073780638771974), (19,0.017826194602624412), (20,0.01559792027729636), (21,0.012874473879673186), 
    (22,0.01559792027729636), (23,0.013122059915820748), (24,0.014855162168853677), (25,0.01361723198811587), (26,0.01386481802426343), 
    (27,0.012131715771230503), (28,0.009160683337459768), (29,0....

Plot degree distribution and the histogram of node degrees.

input:
    
    val f = Figure()

output:

    ﻿f: breeze.plot.Figure = breeze.plot.Figure@22a4118c
    
input:

    val p1 = f.subplot(2,1,0)
    
output:

    ﻿p1: breeze.plot.Plot = breeze.plot.Plot@1430ab1c
    
input:

    val x = new DenseVector(facebookGraphDistribution map (_._1.toDouble))
    
output:

    ﻿x: breeze.linalg.DenseVector[Double] = DenseVector(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 
    13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0, 31.0, 
    32.0, 33.0, 34.0, 35.0, 36.0, 37.0, 38.0, 39.0, 40.0, 41.0, 42.0, 43.0, 44.0, 45.0, 46.0, 47.0, 48.0, 49.0, 50.0, 
    51.0, 52.0, 53.0, 54.0, 55.0, 56.0, 57.0, 58.0, 59.0, 60.0, 61.0, 62.0, 63.0, 64.0, 65.0, 66.0, 67.0, 68.0, 69.0, 
    70.0, 71.0, 72.0, 73.0, 74.0, 75.0, 76.0, 77.0, 78.0, 79.0, 80.0, 81.0, 82.0, 83.0, 84.0, 85.0, 86.0, 87.0, 88.0, 
    89.0, 90.0, 91.0, 92.0, 93.0, 94.0, 95.0, 96.0, 97.0, 98.0, 99.0, 100.0, 101.0, 102.0, 103.0, 104.0, 105.0, 106.0, 
    107.0, 108.0, 109.0, 110.0, 111.0, 112.0, 113.0, 114.0, 115.0, 116.0, 117.0, 119.0, 120.0, 121.0, 122.0, 123.0, ...

input:

    val y = new DenseVector(facebookGraphDistribution map (_._2))

output:

    ﻿y: breeze.linalg.DenseVector[Double] = DenseVector(0.018568952711067097, 0.024263431542461005, 0.0230255013617232, 
    0.024511017578608567, 0.0230255013617232, 0.024263431542461005, 0.024263431542461005, 0.027482050012379303, 
    0.02475860361475613, 0.02352067343401832, 0.020054468927952464, 0.020302054964100025, 0.01955929685565734, 
    0.02153998514483783, 0.026244119831641495, 0.020302054964100025, 0.018816538747214655, 0.018073780638771974, 
    0.017826194602624412, 0.01559792027729636, 0.012874473879673186, 0.01559792027729636, 0.013122059915820748, 
    0.014855162168853677, 0.01361723198811587, 0.01386481802426343, 0.012131715771230503, 0.009160683337459768, 
    0.009408269373607328, 0.009903441445902451, 0.009408269373607328, 0.010893785590492696, 0.008665511265164644, 
    0.010646199554345134, 0.00891309...

input:

    p1.xlabel = "Degrees"

output:

    ﻿p1.xlabel: String = Degrees

input:

    p1.ylabel = "Distribution"

output:

    ﻿p1.ylabel: String = Distribution

input:

    p1 += plot(x, y)

output:

    ﻿res16: breeze.plot.Plot = breeze.plot.Plot@1430ab1c
    
input:

    p1.title = "Degree distribution"

output:

    ﻿p1.title: String = Degree distribution

input:

    val p2 = f.subplot(2,1,1)
    
output:

    ﻿p2: breeze.plot.Plot = breeze.plot.Plot@1ee65ab1
    INSERT GRAPH HERE

input:

    val facebookGraphDegrees = facebookGraph.degrees.map(_._2).collect()

output:

    ﻿facebookGraphDegrees: Array[Int] = Array(4, 15, 38, 24, 3, 8, 63, 6, 18, 17, 55, 11, 22, 10, 36, 17, 16, 8, 25, 139,
     19, 156, 34, 124, 53, 22, 31, 2, 77, 20, 7, 27, 36, 8, 142, 72, 39, 29, 45, 7, 32, 110, 2, 48, 18, 198, 79, 38, 13, 
     20, 60, 5, 61, 27, 4, 41, 37, 9, 13, 67, 190, 44, 14, 41, 140, 21, 21, 11, 84, 35, 52, 114, 18, 77, 91, 70, 44, 40, 
     23, 49, 7, 59, 6, 41, 31, 5, 44, 33, 33, 9, 31, 18, 12, 15, 10, 245, 13, 34, 61, 6, 6, 14, 2, 9, 60, 7, 46, 36, 8, 
     10, 22, 9, 13, 13, 10, 91, 26, 28, 25, 80, 23, 108, 11, 10, 14, 135, 52, 62, 47, 31, 15, 18, 41, 26, 21, 17, 120, 5, 
     12, 39, 220, 20, 71, 35, 4, 18, 8, 9, 24, 7, 45, 231, 12, 8, 7, 4, 96, 70, 173, 10, 4, 31, 14, 14, 8, 24, 168, 18, 
     9, 53, 103, 25, 72, 45, 100, 193, 95, 2, 15, 126, 17, 13, 23, 18, 9, 7, 9, 2, 142, 43, 11, 11, 4, 2, ...

input:
       
    p2.xlabel = "Degrees"

output:

    ﻿p2.xlabel: String = Degrees

input:

    p2.ylabel = "Histogram of node degrees"
    
output:

    ﻿p2.ylabel: String = Histogram of node degrees
    
input:
    
    p2 += hist(facebookGraphDegrees, 1000)

output:

    ﻿res17: breeze.plot.Plot = breeze.plot.Plot@1ee65ab1
    INSERT GRAPH HERE

