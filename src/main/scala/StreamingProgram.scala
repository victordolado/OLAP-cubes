package KafkaStreaming
import producer.Producer
import consumer.Consumer
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import com.typesafe.scalalogging.Logger
import org.apache.log4j.Logger

object StreamingProgram extends App {
  val produce = new Producer()
  val consume = new Consumer()
  produce.writeToKafka(topic="taxiTopic", filePath="src/main/source/yellow_taxis_resume.csv", separator = ",")
  consume.consumeFromKafka(topic="taxiTopic")
}
