package consumer
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.json4s._
import org.json4s.jackson.JsonMethods._
class Consumer {

  def consumeFromKafka(topic: String) {
    val conf = new SparkConf().setAppName("streamingApp").setMaster("local[4]")
      .set("spark.hadoop.fs.defaultFS", "hdfs://localhost:8020").set("spark.hadoop.fs.defaultFS", "hdfs://localhost:8020");
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

    val values = stream.map(record=>  parse(record.value()).values.asInstanceOf[Map[String, Any]])
    values.saveAsTextFiles("hdfs:/taxiData", "parquet")

    ssc.start()
    ssc.awaitTermination()
  }
}

