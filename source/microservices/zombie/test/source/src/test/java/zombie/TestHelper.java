package zombie;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.yngvark.gridwalls.netcom.GameRpcServer;
import util.lib.InputStreamListener;
import util.lib.ProcessKiller;
import util.lib.ProcessStarter;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TestHelper {
    private Connection connection;
    private GameRpcServer gameInfoRequestHandler;
    private ExecutorService executorService;
    private BlockingQueue<String> eventsFromClient;
    private Process process;

    public void startTest() throws IOException, TimeoutException {
        connection = connect();

        gameInfoRequestHandler = new GameRpcServer(connection, "rpc_queue", (String request) -> "[GameInfo] mapHeight=10 mapWidth=10");

        executorService = Executors.newCachedThreadPool();
        executorService.submit(() -> gameInfoRequestHandler.run());

        eventsFromClient = consumeEventsFromClient(connection);
    }

    private Connection connect() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("rabbithost");
        return factory.newConnection();
    }

    private BlockingQueue<String> consumeEventsFromClient(Connection connection) throws IOException {
        boolean exchangeDurable = false;
        boolean exchangeAutoDelete = true;

        boolean queueDurable = false;
        boolean queueExclusive = false;
        boolean queueAutoDelete = true;
        Map<String, Object> standardArgs = null;

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

    public void startProcess() throws IOException {
        process = ProcessStarter.startProcess(Config.PATH_TO_APP);
    }

    public void waitForProcessOutput(String logText, long time, TimeUnit timeUnit) throws InterruptedException {
        InputStreamListener stdoutListener = new InputStreamListener();
        stdoutListener.listenInNewThreadOn(process.getInputStream());

        InputStreamListener stderrListener = new InputStreamListener();
        stderrListener.listenInNewThreadOn(process.getErrorStream());
    }

    public String getEvent(int timeout, TimeUnit timeUnit) throws InterruptedException {
        System.out.println("Waiting for event...");

        String event = eventsFromClient.poll(timeout, timeUnit);
        if (event == null)
            throw new RuntimeException("Event was null");

        System.out.println("-> " + event);
        return event;
    }

    public void stopTest() throws IOException, IllegalAccessException, InterruptedException, NoSuchFieldException {
        System.out.println("Stopping test.");
        gameInfoRequestHandler.stop();
        executorService.shutdown();
        connection.close();
        ProcessKiller.killUnixProcess(process);
        ProcessKiller.waitForExitAndAssertExited(process, 3, TimeUnit.SECONDS);
    }
}
