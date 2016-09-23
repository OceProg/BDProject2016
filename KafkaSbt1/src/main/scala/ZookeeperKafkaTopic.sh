#!/usr/bin/env bash
#~/kafka_2.11-0.10.0.0/bin/zookeeper-server-start.sh ~/kafka_2.11-0.10.0.0/config/zookeeper.properties &
#~/kafka_2.11-0.10.0.0/bin/kafka-server-start.sh ~/kafka_2.11-0.10.0.0/config/server.properties &
#~/kafka_2.11-0.10.0.0/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic clickstream
#xterm -title "App 1" -e "mycommand; mysecondcommand"
xterm -title "Zookeeper" -hold -e "~/kafka_2.11-0.10.0.0/bin/zookeeper-server-start.sh ~/kafka_2.11-0.10.0.0/config/zookeeper.properties" &
sleep 5
xterm -title "Kafka" -hold -e "~/kafka_2.11-0.10.0.0/bin/kafka-server-start.sh ~/kafka_2.11-0.10.0.0/config/server.properties" &
xterm -title "Topic" -hold -e "~/kafka_2.11-0.10.0.0/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic topicname"