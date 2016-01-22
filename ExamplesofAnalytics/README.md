ExamplesofAnalytics
===================

## Install and Run Spark-Shell

1. Install Java version 7 or 8 from http://java.com/ (restart if necessary).
2. Download The precompiled version of Spark 1.4 with Hadoop 2.6 from http://archive.apache.org/dist/spark/spark-1.4.0/spark-1.4.0-bin-hadoop2.6.tgz
3. Double click spark-1.4.0-bin-hadoop2.6.tgz to extract the files.
4. Open the Terminal and run /path/to/spark/bin/spark-shell to start the spark-shell.
    * /path/to/spark is probably ~/Downloads/spark-1.4.0-bin-hadoop2.6/bin/spark-shell if you extracted spark-1.4.0/spark-1.4.0-bin-hadoop2.6.tgz from your Downloads folder.


## Run the Hands ON Exercises

Instructions to run all of the Hands On exercises from a scala file:

    git clone https://github.com/kevincoakley/GraphAnalyticsforBigDataHandsOn.git
    cd ExamplesofAnalytics
    /path/to/spark/bin/spark-shell --jars lib/gs-core-1.2.jar,lib/gs-ui-1.2.jar,lib/jcommon-1.0.16.jar,lib/jfreechart-1.0.13.jar,lib/breeze_2.10-0.9.jar,lib/breeze-viz_2.10-0.9.jar,lib/pherd-1.0.jar -i Metros.scala 
    /path/to/spark/bin/spark-shell -i JoiningGraphDatasets.scala
    /path/to/spark/bin/spark-shell --jars lib/gs-core-1.2.jar,lib/gs-ui-1.2.jar,lib/jcommon-1.0.16.jar,lib/jfreechart-1.0.13.jar,lib/breeze_2.10-0.9.jar,lib/breeze-viz_2.10-0.9.jar,lib/pherd-1.0.jar -i Facebook.scala 
    /path/to/spark/bin/spark-shell --driver-memory 2G --jars lib/gs-core-1.2.jar,lib/gs-ui-1.2.jar,lib/jcommon-1.0.16.jar,lib/jfreechart-1.0.13.jar,lib/breeze_2.10-0.9.jar,lib/breeze-viz_2.10-0.9.jar,lib/pherd-1.0.jar -i CoAuthorship.scala
