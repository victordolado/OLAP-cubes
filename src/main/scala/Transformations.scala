import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import com.typesafe.scalalogging.Logger
import org.apache.log4j.Logger

class Transformations(data: String) {
  val conf = new SparkConf().setAppName("streamingApp").setMaster("local")
  val sc = new SparkContext(conf)
  val textFile = sc.textFile("/home/usuario/Documentos/repositories/OLAP-cubes/src/main/source/prueba.txt")
  print(textFile.first())

}
