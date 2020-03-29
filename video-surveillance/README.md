# Video Surveillance
This project demonstrates how to detect the frame change for camera streams

## Prerequisites
You need to have the following installed on your machine.

JDK 13   https://www.oracle.com/java/technologies/javase-jdk13-downloads.html

Maven 3.3.9   https://maven.apache.org/download.cgi

Kafka   >=2.11-0.10.2.0   http://kafka.apache.org/downloads.html

Flink   1.9.0     Include in the maven repository

OpenCV    3.2.0   http://opencv.org/releases.html

## Execution Instruction

1. Keep the Zookeeper and Kafka servers up and running
https://zookeeper.apache.org/doc/r3.4.14/zookeeperStarted.html

2. We need OpenCV native libraries to run this application. 
Mention them in the VM options in IntelliJ or add the below while running through the command line

-Djava.library.path="<Your download path>/opencv-3.2.0/build/lib" 

3. Create a new Kafka topic using below 
```
kafka-topics.sh --create --zookeeper localhost:2181 --topic video-stream-event --replication-factor 1 --partitions 3
```

4. Specify the input path camera.url, output path processed.output.dir and the log directories for stream-collector.log, stream-processor.log are created as mentioned in your code.

5. Compile and package the Jar file
```
mvn clean package
```

6. Start the video stream processor first
```
java -Djava.library.path=<opencv library path> -jar target\video-stream-processor-1.0.0.jar
```

7. Start the video stream collector
```
java -Djava.library.path=<opencv library path> -jar target\video-stream-collector-1.0.0.jar
```
There you go!

Check your output directory(processed.output.dir) and see the processed frames.