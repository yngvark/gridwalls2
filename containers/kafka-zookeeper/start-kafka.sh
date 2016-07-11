#!/bin/bash

# Create configuration
#ZOO_IP=`echo $ZOO_PORT | grep -o -E "tcp://([0-9.]+):" | sed "s/tcp:\/\///g" | sed "s/://g"`
#ZOO_PORT=`echo $ZOO_PORT | grep -o -E ":[0-9]+" | sed "s/://g"`

ADVERTISED_HOST=`ip addr show eth0 | grep -oP "inet \K([0-9\.]+)"`

cd /app/kafka/config
perl -pe "s/zookeeper.connect=[a-zA-Z0-9:]+\n/zookeeper.connect=zoo:2181\n/g" server.properties > server-kafka-container.properties
sed -r -i "s/#(advertised.listeners)=(.*)/\1=PLAINTEXT:\/\/$ADVERTISED_HOST:9092/g" server-kafka-container.properties

echo ------------- Environment variables
env | sort


echo ------------- Created server-kafka-container.properties. Adjustements from default:
echo zookeeper.connect=zoo:2181
echo ADVERTISED_HOST: $ADVERTISED_HOST:

echo ------------- Starting kafka...

# Start kafka
cd ..
bin/kafka-server-start.sh config/server-kafka-container.properties
