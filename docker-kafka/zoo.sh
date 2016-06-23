docker rm zookeeper
docker run --net=dockerkafka_zoo_kafka -p 2181:2181 --name zookeeper kafka-zookeeper /app/kafka/bin/zookeeper-server-start.sh /app/kafka/config/zookeeper.properties
