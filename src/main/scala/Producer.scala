package producer
import java.text.{DateFormat, SimpleDateFormat}
import java.util.Properties
import java.util.Date
import java.time._
import org.apache.kafka.clients.producer._
import org.json4s.DefaultFormats
import com.google.gson.Gson

case class Json(diff_pickup_dropoff: Double, passenger_count: Double, trip_distance: Double, total_amount: Double)

class Producer {

  def writeToKafka(topic: String, filePath: String, separator: String): Unit = {
    val bufferedSource = scala.io.Source.fromFile(filePath)
    for (line <- bufferedSource.getLines.drop(1)) {
      val cols = line.split(",")
      val day = cols(1).split(" ")(0)

      val tpep_pickup_string = cols(1).split(" ")(1).split(':')
      val tpep_pickup_minutes = tpep_pickup_string(0).toFloat*60 + tpep_pickup_string(1).toFloat + tpep_pickup_string(2).toFloat/60

      val tpep_dropoff_string = cols(2).split(" ")(1).split(':')
      val tpep_dropoff_minutes = tpep_dropoff_string(0).toFloat*60 + tpep_dropoff_string(1).toFloat + tpep_dropoff_string(2).toFloat/60

      val diff_pickup_dropoff = tpep_dropoff_minutes - tpep_pickup_minutes

      val passenger_count = cols(3).toFloat
      val trip_distance = cols(4).toFloat
      val total_amount = cols(16).toFloat

      val dataToSend = Json(diff_pickup_dropoff, passenger_count, trip_distance, total_amount)
      val gson = new Gson
      val jsonString = gson.toJson(dataToSend)
      val props = new Properties()
      props.put("bootstrap.servers", "localhost:9092")
      props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
      props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
      val producer = new KafkaProducer[String, String](props)
      val record = new ProducerRecord[String, String](topic, day, jsonString)
      producer.send(record)
      producer.close()
    }
    bufferedSource.close
  }
}