package timeDuration
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{StringType, StructField, StructType}

object TimeProgram extends App{

  val spark = SparkSession.builder().appName("Prueba").config("spark.hadoop.fs.defaultFS", "hdfs://localhost:8020").master("local[4]").getOrCreate()

//  val schema = StructType(List(
//    StructField("diff_pickup_dropoff", StringType),
//    StructField("passenger_count", StringType),
//    StructField("trip_distance", StringType),
//    StructField("total_amount", StringType)
//  )
//  )

  val df = spark.read.format("parquet").parquet("/taxisDF/")
  df.show(false)
}
