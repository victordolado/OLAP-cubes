package timeDuration
import org.apache.spark.sql.SparkSession

object TimeProgram extends App{

  val spark = SparkSession.builder().appName("Prueba").config("spark.hadoop.fs.defaultFS", "hdfs://localhost:8020").master("local[4]").getOrCreate()

  val df = spark.read.format("parquet").parquet("/taxisDF/")
  df.show(truncate=false)
  df.createGlobalTempView("taxisData")

  spark.sql("SELECT day, avg(passenger_count) as passengerAvg, avg(diff_pickup_dropoff) as travelTimeAvg," +
    " avg(trip_distance) as tripDistanceAvg, avg(total_amount) as totalAmountAvg FROM global_temp.taxisData GROUP BY day").show()
}
