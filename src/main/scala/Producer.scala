package producer
import java.text.{DateFormat, SimpleDateFormat}
import java.util.Properties
import java.util.Date
import org.apache.kafka.clients.producer._
import org.json4s.DefaultFormats
import com.google.gson.Gson

case class Json(day: String, tpep_pickup_datetime: String, tpep_dropoff_datetime: String, passenger_count: Integer,
                trip_distance: Float, total_amount: Float)

class Producer {

  def writeToKafka(topic: String, filePath: String, separator: String): Unit = {
    val bufferedSource = scala.io.Source.fromFile(filePath)
    for (line <- bufferedSource.getLines.drop(1)) {
      val cols = line.split(",")
      val day = cols(1).split(" ")(0)
      val tpep_pickup_datetime = cols(1).split(" ")(1)
      val tpep_dropoff_datetime = cols(2).split(" ")(1)
      val passenger_count = cols(3).toInt
      val trip_distance = cols(4).toFloat
      val total_amount = cols(16).toFloat
      val dataToSend = Json(day, tpep_pickup_datetime, tpep_dropoff_datetime,passenger_count, trip_distance, total_amount)
      val gson = new Gson
      val jsonString = gson.toJson(dataToSend)
      val props = new Properties()
      props.put("bootstrap.servers", "localhost:9092")
      props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
      props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
      val producer = new KafkaProducer[String, String](props)
      val record = new ProducerRecord[String, String](topic, cols(0), jsonString)
      producer.send(record)
      producer.close()
    }
    bufferedSource.close
  }
}