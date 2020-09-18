package KafkaStreaming
import producer.Producer
import consumer.Consumer
object StreamingProgram extends App {
  println("Hello world!")
  val produce = new Producer()
  val consume = new Consumer()
  produce.writeToKafka(topic="taxiTopic", filePath="src/main/source/yellow_taxis_resume.csv", separator = ",")
  consume.consumeFromKafka(topic="taxiTopic")
  }
