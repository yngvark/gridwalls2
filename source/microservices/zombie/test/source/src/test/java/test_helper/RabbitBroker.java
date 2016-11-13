package test_helper;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.yngvark.gridwalls.netcom.GameRpcServer;
import com.yngvark.gridwalls.netcom.RpcRequestHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

public class RabbitBroker implements Broker {
    private Connection connection;
    private Channel serverMessages;

    @Override
    public void connect() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("rabbithost");
        connection = factory.newConnection();
        setupServerQueue();
    }

    private void setupServerQueue() throws IOException {
        serverMessages = connection.createChannel();

        boolean exchangeDurable = false;
        boolean exchangeAutoDelete = true;
        Map<String, Object> standardArgs = null;

        serverMessages.exchangeDeclare("ServerMessages", "fanout", exchangeDurable, exchangeAutoDelete, standardArgs);

        boolean queueDurable = false;
        boolean queueExclusive = false;
        boolean queueAutoDelete = true;

        serverMessages.queueDeclare("server_messages", queueDurable, queueExclusive, queueAutoDelete, standardArgs);
        serverMessages.queueBind("server_messages", "ServerMessages", "");
    }

    @Override
    public BlockingQueue<String> consumeEventsFromClient() throws IOException {
        boolean exchangeDurable = false;
        boolean exchangeAutoDelete = true;
        Map<String, Object> standardArgs = null;

        String zombieMovedQueueName = "zombie_moved_queue";
        Channel eventsFromClientChannel = connection.createChannel();
        eventsFromClientChannel.exchangeDeclare("ZombieMoved", "fanout", exchangeDurable, exchangeAutoDelete, standardArgs);

        boolean queueDurable = false;
        boolean queueExclusive = false;
        boolean queueAutoDelete = true;
        eventsFromClientChannel.queueDeclare(zombieMovedQueueName, queueDurable, queueExclusive, queueAutoDelete, standardArgs);
        eventsFromClientChannel.queueBind(zombieMovedQueueName, "ZombieMoved", "");

        BlockingQueue<String> blockingBrokerQueue = new ArrayBlockingQueue<>(1);

        Consumer consumer = new DefaultConsumer(eventsFromClientChannel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                    AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");

                try {
                    blockingBrokerQueue.put(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        System.out.println("Consuming messages for queue: " + zombieMovedQueueName);
        eventsFromClientChannel.basicConsume(zombieMovedQueueName, true /* autoAck */, consumer);
        return blockingBrokerQueue;
    }

    @Override
    public GameRpcServer createRpcServer(String queueName, RpcRequestHandler requestHandler) {
        return new GameRpcServer(connection, queueName, requestHandler);
    }

    @Override
    public void close() throws IOException {
        connection.close();
    }

    @Override
    public void publishServerMessage(String msg) throws IOException {
        serverMessages.basicPublish("ServerMessages", "", null, msg.getBytes());
    }
}
