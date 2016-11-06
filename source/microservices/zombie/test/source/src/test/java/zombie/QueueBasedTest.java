package zombie;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.yngvark.gridwalls.netcom.GameRpcServer;
import org.junit.jupiter.api.Test;
import zombie.lib.InputStreamListener;
import zombie.lib.ProcessKiller;
import zombie.lib.ProcessStarter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class QueueBasedTest {
    @Test
    public void should_produe_zombie_moves_after_receiving_gameconfig()
            throws IOException, TimeoutException, InterruptedException, NoSuchFieldException, IllegalAccessException {
        // Given
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("rabbithost");
        Connection connection = factory.newConnection();

        boolean exchangeDurable = false;
        boolean exchangeAutoDelete = true;

        boolean queueDurable = false;
        boolean queueExclusive = false;
        boolean queueAutoDelete = true;
        Map<String, Object> standardArgs = null;

        // Listen for GameInfo RPC-calls from client.
        ExecutorService executorService = Executors.newCachedThreadPool();
        GameRpcServer gameInfoRequestHandler = new GameRpcServer(connection, "rpc_queue", (String request) -> "[GameInfo] mapHeight=10 mapWidth=10");
        executorService.submit(() -> gameInfoRequestHandler.run());

        // Listen for events from client.
        String zombieMovedQueueName = "zombie_moved_queue";
        Channel eventsFromClientChannel = connection.createChannel();
        eventsFromClientChannel.exchangeDeclare("ZombieMoved", "fanout", exchangeDurable, exchangeAutoDelete, standardArgs);
        eventsFromClientChannel.queueDeclare(zombieMovedQueueName, queueDurable, queueExclusive, queueAutoDelete, standardArgs);
        eventsFromClientChannel.queueBind(zombieMovedQueueName, "ZombieMoved", "");

        BlockingQueue<String> blockingBrokerQueue = new ArrayBlockingQueue<>(1);

        Consumer consumer = new DefaultConsumer(eventsFromClientChannel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                    AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(new Date() +"-> Received '" + message + "'");

                try {
                    blockingBrokerQueue.put(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        System.out.println("Consuming messages for queue: " + zombieMovedQueueName);
        eventsFromClientChannel.basicConsume(zombieMovedQueueName, true /* autoAck */, consumer);

        // When
        Process process = ProcessStarter.startProcess(Config.PATH_TO_APP);

        // Get process output
        InputStreamListener stdoutListener = new InputStreamListener();
        stdoutListener.listenInNewThreadOn(process.getInputStream());

        InputStreamListener stderrListener = new InputStreamListener();
        stderrListener.listenInNewThreadOn(process.getErrorStream());

        stdoutListener.waitFor("Receiving game config.", 5, TimeUnit.SECONDS);

        // Read queue.
        System.out.println("Waiting for a few zombie move events.");
        for (int i = 0; i < 2; i++) {
            String event = blockingBrokerQueue.poll(1200, TimeUnit.MILLISECONDS);
            System.out.println("Processing event (" + i + "): " + event);
            if (event == null)
                throw new RuntimeException("Event was null");

            assertTrue(event.startsWith("[ZombieMoved]"));
        }

        // Finally
        System.out.println("Stopping test.");
        gameInfoRequestHandler.stop();
        executorService.shutdown();
        connection.close();
        ProcessKiller.killUnixProcess(process);
        ProcessKiller.waitForExitAndAssertExited(process, 3, TimeUnit.SECONDS);
    }

}
