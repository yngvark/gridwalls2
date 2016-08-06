package com.yngvark.gridwalls.netcom;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

import java.io.IOException;

@Deprecated
public class RpcServer2 {
    private static final String RPC_QUEUE_NAME = "rpc_queue";

    private final Connection connection;

    private Channel channel = null;
    private String consumerTag = "";
    private Object channelCloseSignal = new Object();

    public RpcServer2(Connection connection) {
        this.connection = connection;
    }

    public void run () {
        if (channel != null)
            throw new RuntimeException("Server is already running.");

        try {
            channel = connection.createChannel();

            channel.exchangeDeclare("GameInfo", "fanout", true);

            channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);
            channel.basicQos(1);
            QueueingConsumer consumer = new QueueingConsumer(channel);
            channel.basicConsume(RPC_QUEUE_NAME, false, consumer);

            System.out.println("Awaiting RPC requests");

            while (true) {
                String response = null;

                QueueingConsumer.Delivery delivery = consumer.nextDelivery();

                BasicProperties props = delivery.getProperties();
                BasicProperties replyProps = new BasicProperties
                        .Builder()
                        .correlationId(props.getCorrelationId())
                        .build();

                try {
                    String message = new String(delivery.getBody(),"UTF-8");
                    System.out.println("Received message: " + message);
                    response = "[GameInfo] mapHeight=10 mapWidth=10";
                    System.out.println("Response: " + response);

                }
                catch (Exception e){
                    System.out.println(" [.] " + e.toString());
                    response = "";
                }
                finally {
                    channel.basicPublish( "", props.getReplyTo(), replyProps, response.getBytes("UTF-8"));
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
            }
        }
        catch (ShutdownSignalException | ConsumerCancelledException e) {
            System.out.println("Server stopped.");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (channel != null) {
                try {
                    channel.close();
                    channel = null;
                }
                catch (Exception ignore) {}
            }

            channelCloseSignal.notify();
        }
    }

    public void stop() {
        System.out.println("Stopping " + this.getClass().getSimpleName());
        if (channel == null) // TODO: Should not use channel across threads
            return;

        try {
            channel.basicCancel(consumerTag); // Will cause a ConsumerCancelledException
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            System.out.println("Waiting for channelCloseSignal.");
            channelCloseSignal.wait(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Stopping " + this.getClass().getSimpleName() + "... done.");
    }
}