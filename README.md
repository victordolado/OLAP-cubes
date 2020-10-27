## OLAP-CUBES
### Project description

<div style="text-align: justify">
One of the most expensive processes in Big Data environments and analytics environments in general, is having to perform "full-scan" operations to obtain metrics and data analysis. That is why the generation of OLAP cubes, when it is possible, for grouping data in real time, and before being persisted, it is one of the most used techniques in environments with data lakes. Thus, the idea is to create a Spark Streaming application that receives data through Apache Kafka and, based on a configuration, generate OLAP cubes and persist them in MongoDB. On the other hand, the "raw" data will be persisted in HDFS with Apache Parquet format. The objective is the acceleration of queries through the on-the-fly calculation of data in streaming before being persisted.
</div>

### Project configuration

* Set up Apache Kafka and MongoDB run the following command.

    ` docker-compose -f src/main/configuration/project-compose.yml`


* Launch a Docker container for single HDFS node

    `	docker pull mdouchement/hdfs`

* Then create the container and get a bash interpreter

    `	docker run -p 22022:22 -p 8020:8020 -p 50010:50010 -p 50020:50020 -p 50070:50070 -p 50075:50075 -p 111:111 -p 2049:2049 -it mdouchement/hdfs`


### Run the project with sbt

Move to root directory and run the following commands in the terminal

`sbt compile`

`sbt run target/scala-2.12/classes/KafkaStreaming/StreamingProgram.class`
