docker rm kafka
docker run --net=dockerkafka_zoo_kafka -it --link zookeeper:zoo --name kafka -p 9092:9092 kafka-zookeeper kafka/bin/kafka-server-start.sh kafka/config/server.properties
