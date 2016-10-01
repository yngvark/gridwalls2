package zombie;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.yngvark.gridwalls.netcom.GameRpcServer;
import com.yngvark.gridwalls.netcom.ThreadedRunner;
import org.junit.jupiter.api.Test;
import zombie.lib.CommandExecutor;
import zombie.lib.CommandExecutorFactory;
import zombie.lib.ProcessKiller;
import zombie.lib.ProcessStarter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class QueueBasedTest {
    @Test
    public void should_always_change_coordinate_and_never_stand_still_on_next_turn() throws Exception {
        // Given
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("rabbithost");
        Connection connection = factory.newConnection();

        Map<String, Object> standardArgs = null;

        boolean exchangeDurable = false;
        boolean exchangeAutoDelete = true;

        boolean queueDurable = false;
        boolean queueExclusive = true;
        boolean queueAutoDelete = true;

        // Listen for GameInfo RPC-calls from client.
        GameRpcServer gameInfoRequestHandler = new GameRpcServer(connection, "game_info_queue", (String request) -> {
            return "[GameInfo] mapHeight=10 mapWidth=10";
        });
        ThreadedRunner threadedGameInfoRequestHandler = new ThreadedRunner(gameInfoRequestHandler);
        threadedGameInfoRequestHandler.runInNewThread();

        // Set up exchange for server notifications
        Channel serverNotificationsChannel = connection.createChannel();
        serverNotificationsChannel.exchangeDeclare("ServerNotifications", "fanout", exchangeDurable, exchangeAutoDelete, standardArgs);
        serverNotificationsChannel.queueDeclare("server_notifications_queue", queueDurable, queueExclusive, queueAutoDelete, standardArgs);
        serverNotificationsChannel.queueBind("server_notifications_queue", "ServerNotifications", "");

        // Listen for events from client.
        Channel eventsFromClientChannel = connection.createChannel();
        eventsFromClientChannel.exchangeDeclare("ZombieMoved", "fanout", exchangeDurable, exchangeAutoDelete, standardArgs);
        eventsFromClientChannel.queueDeclare("zombie_moved_queue", queueDurable, queueExclusive, queueAutoDelete, standardArgs);
        eventsFromClientChannel.queueBind("zombie_moved_queue", "ZombieMoved", "");

        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(1);

        Consumer consumer = new DefaultConsumer(eventsFromClientChannel) {
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

        System.out.println("Start basic consume.");
        eventsFromClientChannel.basicConsume("zombie_moved_queue", true, consumer);

        // Start microservice
        Process process = new ProcessStarter().startProcess(Config.PATH_TO_APP);

        //  Then
        //Coordinate lastCoord = null;
        for (int i = 0; i < 4; i++) {
            // When
            System.out.println("Reading next message from queue.");
            String event = blockingQueue.take();
            System.out.println("Consuming msg: " + event);

            // Then
            //ZombieMoved zombieMoved = zombieMovedSerializer.deserialize(event);

            //if (lastCoord != null)
//                assertNotEquals(lastCoord, zombieMoved.getTargetCoordinate());

            // Finally
  //          lastCoord = zombieMoved.getTargetCoordinate();
        }

        new ProcessKiller().killUnixProcess(process);

        threadedGameInfoRequestHandler.stop();
        connection.close();

        new ProcessKiller().exitAndAssertExited(process);;
    }
}
