package consumer

import org.apache.spark.sql.functions._
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.json4s._
import org.json4s.jackson.JsonMethods._
import com.mongodb.spark.sql._
import org.apache.spark.sql.{SQLContext}


class Consumer {

  def consumeFromKafka(topic: String) {


    val conf = new SparkConf().setAppName("streamingApp").setMaster("local[4]")
      .set("spark.hadoop.fs.defaultFS", "hdfs://localhost:8020")
      .set("spark.hadoop.fs.defaultFS", "hdfs://localhost:8020")
      .set("mapreduce.fileoutputcommitter.algorithm.version", "2")
      .set("spark.mongodb.input.uri", "mongodb://localhost:27017/")
      .set("spark.mongodb.output.uri", "mongodb://localhost:27017/")
      .set("spark.mongodb.input.database", "local")
      .set("spark.mongodb.input.collection", "OLAPCubes")
      .set("spark.mongodb.output.database", "local")
      .set("spark.mongodb.output.collection", "OLAPCubes")
      .set("spark.app.id", "Mongo")


    val sc = new SparkContext(conf)
    val ssc = new StreamingContext(sc, Seconds(1))
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer",
      "value.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer",
      "group.id" -> "consumer-group",
      "auto.offset.reset" -> "latest",
    )

    val topics = Array(topic)
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )

    val sqlContext = new SQLContext(sc)
    import sqlContext.implicits._

    val values = stream.map(record =>  parse(record.value()).values.asInstanceOf[Map[String, Double]])

    values.foreachRDD(rdd => if(!rdd.partitions.isEmpty)
      rdd.map(x => (x("diff_pickup_dropoff"), x("passenger_count"), x("trip_distance"), x("total_amount")))
      .toDF("diff_pickup_dropoff", "passenger_count", "trip_distance", "total_amount")
      .write.format("parquet").mode("append").save("/taxisDF/"))

    stream.map(record => (record.key, Tuple5(
      parse(record.value()).values.asInstanceOf[Map[String, Double]]("diff_pickup_dropoff"),
      parse(record.value()).values.asInstanceOf[Map[String, Double]]("passenger_count"),
      parse(record.value()).values.asInstanceOf[Map[String, Double]]("trip_distance"),
      parse(record.value()).values.asInstanceOf[Map[String, Double]]("total_amount"),
      1)))
      .foreachRDD(rdd => rdd.reduceByKey((x, y) => (x._1.toFloat + y._1.toFloat,
        x._2.toFloat + y._2.toFloat,
        x._3.toFloat + y._3.toFloat,
        x._4.toFloat + y._4.toFloat,
        x._5 + y._5))
        .map(x => (x._1,
          x._2._1/x._2._5,
          x._2._2/x._2._5,
          x._2._3/x._2._5,
          x._2._4/x._2._5))
        .toDF("Day","travelTimeAvg(Min)", "passengerAvg", "tripDistanceAvg(Miles)", "totalAmountAvg(USD)")
        .withColumn("Day",to_date($"Day", "yyy-MM-dd")).write.mode("append").mongo())

    ssc.start()
    ssc.awaitTermination()
  }
}

