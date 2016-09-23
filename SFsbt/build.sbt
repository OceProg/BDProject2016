import sun.security.tools.PathList

//name := "SFsbt"
//version := "1.0"
//scalaVersion := "2.10.5"
//libraryDependencies += "org.apache.spark" %% "spark-core" % "1.6.2"
//libraryDependencies += "org.apache.kafka" % "kafka-clients" % "0.10.0.1"
//libraryDependencies += "com.datastax.spark" %% "spark-cassandra-connector" % "1.6.2" withSources() withJavadoc()
//libraryDependencies += "org.apache.spark" % "spark-sql_2.10" % "1.6.2"

//libraryDependencies ++= Seq(
//  "org.slf4j" % "slf4j-api"       % "1.7.7",
//  "org.slf4j" % "jcl-over-slf4j"  % "1.7.7"
//).map(_.force())
//
//libraryDependencies ~= { _.map(_.exclude("org.slf4j", "slf4j-jdk14")) }

//resolvers += "Spark Packages Repo" at "https://dl.bintray.com/spark-packages/maven"
//libraryDependencies += "datastax" % "spark-cassandra-connector" % "1.6.0-s_2.10"

lazy val root = (project in file(".")).
  settings(
    name := "SFsbt",
    version := "1.0",
    scalaVersion := "2.10.5",
    mainClass in Compile := Some("main.sparkCassandraConnectorExample")
  )

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "1.6.2",
  "org.apache.kafka" % "kafka-clients" % "0.10.0.1",
  "com.datastax.spark" %% "spark-cassandra-connector" % "1.6.2",
  "org.apache.spark" % "spark-sql_2.10" % "1.6.2"
)
//"org.apache.spark" %% "spark-streaming" % "1.2.0" % "provided",
//"org.apache.spark" % "spark-streaming-twitter_2.10" % "1.2.0"

// META-INF discarding
//mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
//{
//  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
//  case x => MergeStrategy.first
//}
//}