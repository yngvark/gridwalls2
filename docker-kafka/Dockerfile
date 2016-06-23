FROM java:8

RUN apt-get update
RUN apt-get install -y sudo
RUN sudo apt-get install nano

ENV VERSION 0.10.0.0
ENV KAFKA kafka_2.11-"$VERSION"

RUN mkdir --parents /app/kafka && \
	cd /app/kafka && \
	wget http://apache.uib.no/kafka/"$VERSION"/"$KAFKA".tgz -o wget.log
RUN cd /app/kafka && \
	tar -xzf "$KAFKA".tgz && \
	cd "$KAFKA" && \
	mv * .. && \
	cd .. && \
	rm -r "$KAFKA".tgz && \
	rm -r $KAFKA

ADD start-kafka.sh /app

WORKDIR /app

# 2181 is Zookeeper, 9092 is Kafka

