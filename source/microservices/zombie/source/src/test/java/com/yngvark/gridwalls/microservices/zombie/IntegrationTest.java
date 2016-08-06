package com.yngvark.gridwalls.microservices.zombie;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.yngvark.gridwalls.core.Coordinate;
import com.yngvark.gridwalls.core.CoordinateSerializer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class IntegrationTest {
    @Test
    @Disabled
    public void should_always_change_coordinate_and_never_stand_still_on_next_turn() throws IOException, TimeoutException, InterruptedException {
        // Given
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("rabbithost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare("ZombieMoved", "fanout", true);
        String queueName = "whatever2";
        channel.queueDeclare(queueName, true, true, false, null);
        channel.queueBind(queueName, "ZombieMoved", "");


        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(1);

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                    AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + message + "'");

                try {
                    blockingQueue.put(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        System.out.println("Start basic consume");
        channel.basicConsume(queueName, true, consumer);
        System.out.println("Start basic consume - done");

        ZombieMovedSerializer zombieMovedSerializer = new ZombieMovedSerializer(new CoordinateSerializer());

        // Start microservice
        //Hvordan starte MSen? Med JUNIT? Eller anta at noen har starta Main allerede?


        //  Then
        Coordinate lastCoord = null;
        for (int i = 0; i < 4; i++) {
            // When
            String event = blockingQueue.take();
            System.out.println("Consuming msg: " + event);

            // Then
            ZombieMoved zombieMoved = zombieMovedSerializer.deserialize(event);

            if (lastCoord != null)
                assertNotEquals(lastCoord, zombieMoved.getTargetCoordinate());

            // Finally
            lastCoord = zombieMoved.getTargetCoordinate();
        }

        channel.close();
        connection.close();
    }
}
