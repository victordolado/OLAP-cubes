## OLAP cubes creation with Apache Kafka and Apache Spark
### Project description

<div style="text-align: justify">
One of the most expensive processes in Big Data environments and analytics environments in general, is having to perform "full-scan" operations to obtain metrics and data analysis. That is why the generation of OLAP cubes, when it is possible, for grouping data in real time, and before being persisted, it is one of the most used techniques in environments with data lakes. Thus, the idea is to create a Spark Streaming application that receives data through Apache Kafka and, based on a configuration, generate OLAP cubes and persist them in MongoDB. On the other hand, the "raw" data will be persisted in HDFS with Apache Parquet format. The objective is the acceleration of queries through the on-the-fly calculation of data in streaming before being persisted.
</div>

### Project architecture

Real-time event processing project architecture.

![TFMSchema](/src/main/docImages/TFMSchema.png)

### Project configuration

* Set up Apache Kafka and MongoDB running the following command.

    ` docker-compose -f src/main/configuration/project-compose.yml up -d`


* Launch a Docker container for single HDFS node

    `	docker pull mdouchement/hdfs`

* Then create the container and get a bash interpreter

    `	docker run -p 22022:22 -p 8020:8020 -p 50010:50010 -p 50020:50020 -p 50070:50070 -p 50075:50075 -p 111:111 -p 2049:2049 -it mdouchement/hdfs`


### Run the project with sbt

Move to root directory and run the following commands in the terminal

`sbt compile`

`sbt run`

### Program description and expected results

The source data could be taken from:

https://www1.nyc.gov/site/tlc/about/tlc-trip-record-data.page

The source data sample for this project is stored in the following path:

​		`src/main/source/yellow_taxis_sample.csv`

The following columns are selected from the csv file:

* **day**
* **tpep_pickup_datetime**
* **tpep_dropoff_datetime**
* **passenger_count**
* **trip_distance**
* **total_amount** 

The columns "tpep_pickup_datetime" and "tpep_dropoff_datetime" are transformed to calculate the total time of the taxi trip duration. This output column is called "diff_pickup_dropoff". Then a basic parsing of the data is carried out to send them to the kafka topic called "taxiTopic" with the following format:

<img src="/src/main/docImages/taxiTopicAllData.png" alt="taxiTopicAllData" style="zoom:67%;" />

Once the data is sent to the topic, it is processed. The processing consists of two parts:

1. On the one hand, the raw data is stored in HDFS in the /taxisDF/ path as a dataframe in parquet format. 

   ![](/src/main/docImages/taxiDataHDFS.png)

   ![HDFSDataFrameValues](/src/main/docImages/HDFSDataFrameValues.png)

2. On the other hand, the following transformations are carried out. The data is grouped by the "day" key and then the following calculations are done:
* **The average of each trip duration**
* **The average of the number of passengers per trip**
* **The average of each trip distance**
* **The average of each trip price**

Then, these transformations are ingested in MongoDB in the OLAPCubes collection of the local database.

<img src="/src/main/docImages/mongoDBOLAP.png" alt="mongoDBOLAP" style="zoom: 80%;" />



### HDFS data transformations

The data stored in the /taxisDF/ path in HDFS could be transformed to obtain the same data as in the OLAP cubes. To transform the data the following commands must be executed:

​		`sbt compile`

​		`sbt run` 

​		`sbt "runMain timeDuration.TimeProgram"`

The execution of the program returns the following dataframe.

![](/src/main/docImages/HDFSTransformedData.png)
