package producer
import java.util.Properties
import org.apache.kafka.clients.producer._
class Producer {

  def writeToKafka(topic: String, filePath: String, separator: String): Unit = {
    val bufferedSource = scala.io.Source.fromFile(filePath)
    for (line <- bufferedSource.getLines) {
      val cols = line.split(",")
      val props = new Properties()
      props.put("bootstrap.servers", "localhost:9092")
      props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
      props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
      val producer = new KafkaProducer[String, String](props)
      val record = new ProducerRecord[String, String](topic, cols(0), cols(6))
      producer.send(record)
      producer.close()
    }
    bufferedSource.close
  }
}