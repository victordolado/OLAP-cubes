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

### Program description and expected results

The initial data is stored in the following path:

​		`src/main/source/yellow_taxis_resume.csv`

The following columns are selected from the csv file:

* **diff_pickup_dropoff**
* **passenger_count**
* **trip_distance**
* **total_amount** 

A basic parsing of the data is carried out to send them to the kafka topic called "taxiTopic" with the following format:

<img src="/src/main/docImages/taxiTopicAllData.png" alt="taxiTopicAllData" style="zoom:67%;" />

Once the data is sent to the topic, it is processed. The processing consists of two parts:

1. On the one hand, the raw data is saved in HDFS in the root directory with the name of taxiData in Parquet format. 

   ![](/src/main/docImages/taxiDataHDFS.png)

   ![HDFSDataValues](/src/main/docImages/HDFSDataValues.png)

2. On the other hand, the following transformations are carried out. The data is grouped by the "day" key and then the following calculations are done:
* **The average of each trip duration**
* **The average of the number of passengers per trip**
* **The average of each trip distance**
* **The average of each trip price**

Then, these transformations are ingested in MongoDB in the OLAPCubes collection.

<img src="/src/main/docImages/mongoDBOLAP.png" alt="mongoDBOLAP" style="zoom: 80%;" />
