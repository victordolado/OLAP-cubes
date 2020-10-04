name := "ScalaTFMProyect"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies += "org.apache.spark" % "spark-streaming_2.12" % "2.4.6"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "2.4.6",
  "org.apache.spark" %% "spark-sql" % "2.4.6"
)
libraryDependencies += "org.apache.kafka" %% "kafka" % "2.1.0"
dependencyOverrides ++= {
  Seq(
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.6.7.1",
    "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.7",
    "com.fasterxml.jackson.core" % "jackson-core" % "2.6.7"
  )
}

libraryDependencies += "org.apache.spark" %% "spark-streaming-kafka-0-10" % "2.4.6"

