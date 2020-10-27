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

Los datos se almacenan en el fichero situado en la siguiente ruta:

​		`src/main/source/yellow_taxis_resume.csv`

Se seleccionan las siguientes columnas del fichero csv: 

Se cogen las columnas siguientes: **diff_pickup_dropoff**, **passenger_count**, **trip_distance** y **total_amount**. 

Se realiza un parseo básico de los datos para enviarlos al topic de kafka llamado "taxiTopic" con el siguiente formato:

(Foto)

Una vez enviados al topic son leídos y procesados. El procesamiento consta de dos partes:

1. Por un lado se guardan los datos en bruto en HDFS en el directorio raíz con el nombre de taxiData en formato parquet. 

   ![](/src/main/images/taxiDataHDFS.png)

   ![HDFSDataValues](/src/main/images/HDFSDataValues.png)

2. Por otro lado se realizan las siguientes transformaciones. Se agrupa por la clave "día" y se calcula **la media de la duración de los trayectos**, **la media de los pasajeros por trayecto**, **la distancia media de los trayectos** y **la media del precio de cada trayecto**. Posteriormente estas transformaciones son ingestadas en MongoDB en la colección de OLAPCubes.

<img src="/src/main/images/mongoDBOLAP.png" alt="mongoDBOLAP" style="zoom: 80%;" />
