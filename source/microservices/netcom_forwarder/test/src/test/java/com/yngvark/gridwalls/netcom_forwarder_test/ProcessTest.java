package com.yngvark.gridwalls.netcom_forwarder_test;

import com.yngvark.gridwalls.rabbitmq.RabbitConnection;
import com.yngvark.gridwalls.rabbitmq.RabbitPublisher;
import com.yngvark.gridwalls.rabbitmq.RabbitSubscriber;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * These tests are integration tests. You need to do the following steps before running them:
 * - docker run -p 8080:15672 -p 15674:15674 rabbitmq:3-management
 * -
 * - Replace the variable RABBITMQ_IP with the IP to the rabbitmq container. The IP can be found with:
 * docker inspect eloquent_hypatia | grep IPAddress
 * replace eloquent_hypatia with name of docker container.
 *
 * Yeah, it's far from perfect, but it works.
 * - In source project, run
 * ./gradlew installDist
 */
public class ProcessTest {
    public final Logger logger = getLogger(getClass());
    public static final String RABBITMQ_IP = "172.18.0.2";

    // TODO: should_connect_to_broker_before_names_pipes

    @Test
    public void should_create_named_pipes_if_they_dont_exist() throws Exception {
        // Given
        Path to = Paths.get("build/do/create/all/dirs/to_netcom_forwarder");
        Path from = Paths.get("build/do/create/all2/dirs/from_netcom_forwarder");

        deleteIfExists(to);
        deleteIfExists(from);

        // When
        NetworkApp app = NetworkAppFactory.start(to, from);
        app.stopAndFreeResources();

        // Then
        assertTrue(Files.exists(to), to.toString());
        assertTrue(Files.exists(from), to.toString());

        // Finally
        Files.delete(to);
        Files.delete(from);
    }

    private void deleteIfExists(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.delete(path);
        }
    }

    @Test
    public void published_msgs_should_be_sent_to_network_on_correct_topic() throws Exception {
        // Given
        NetworkApp app = NetworkAppFactory.start();

        int numberOfMsgsToSend = 3;
        Future<List<String>> readMessagesFuture = readMessages(
                app.host, app.rabbitConnection, "MyMicroService", numberOfMsgsToSend);

        // When
        for (int i = 0; i < numberOfMsgsToSend; i++) {
            app.outputFileWriter.write("/publishTo MyMicroService Hi this is networkforwarderTest: i=" + i);
        }

        // Record result
        logger.info("Waiting for our subscriber to receive published messages.");
        List<String> networMessagesRead = readMessagesFuture.get(3, TimeUnit.SECONDS);

        // Cleanup
        logger.info("Closing output writer, which should make app stop by itself.");
        app.outputFileWriter.closeStream();
        app.stopAndFreeResources();

        // Then
        assertEquals(3, networMessagesRead.size());
        assertEquals("Hi this is networkforwarderTest: i=0", networMessagesRead.get(0));
        assertEquals("Hi this is networkforwarderTest: i=1", networMessagesRead.get(1));
        assertEquals("Hi this is networkforwarderTest: i=2", networMessagesRead.get(2));
    }

    private Future<List<String>> readMessages(
            String rabbitHost, RabbitConnection rabbitConnection, String topic, int messageCountToExpect) {
        RabbitSubscriber rabbitSubscriber = new RabbitSubscriber(rabbitConnection);
        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<List<String>> receivePublishedMessagesFuture = executorService.submit(() ->
            subscribeAndReceiveMessages(topic, messageCountToExpect, rabbitSubscriber)
        );
        waitForSubscriptionQueueToAppear(rabbitHost + ":15672");
        return receivePublishedMessagesFuture;
    }

    private List<String> subscribeAndReceiveMessages(
            String topic, int messageCountToExpect, RabbitSubscriber rabbitSubscriber) {
        List<String> recordedNetworMessages = new ArrayList<>();
        Lock lock = new Lock();
        Counter counter = new Counter();

        rabbitSubscriber.subscribe("networkForwarderTestReader", topic, (msg) -> {
            logger.info("<<< Networkmsg: " + msg);
            recordedNetworMessages.add(msg);
            counter.increase();
            if (counter.value() == messageCountToExpect) {
                lock.unlock("We have now received expected messages. Continue test.");
            }
        });

        lock.waitForUnlock();
        return recordedNetworMessages;
    }

    @Test
    public void we_should_receive_messages_from_topic_we_subscribe_to() throws Exception {
        // Given
        NetworkApp app = NetworkAppFactory.start();

        app.outputFileWriter.write("/subscribeTo Zombie");
        waitForSubscriptionQueueToAppear(app.host + ":15672");

        // When
        logger.info("Publishing to zombie.");
        RabbitPublisher rabbitPublisher = new RabbitPublisher(app.rabbitConnection);
        int messagesToSend = 3;
        for (int i = 0; i < messagesToSend; i++) {
            rabbitPublisher.publish("Zombie", "Hello this is Zombie " + i);
        }

        // Capture published messages
        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<List<String>> consumeExpectedMessagesFuture = executorService.submit(() ->
            consumeExpectedMessages(app, messagesToSend)
        );
        logger.info("Starting future for consuming expected messages.");
        List<String> receivedMessages = consumeExpectedMessagesFuture.get(3, TimeUnit.SECONDS);
        app.stopAndFreeResources();

        // Then
        assertEquals(messagesToSend, receivedMessages.size());
        for (int i = 0; i < messagesToSend; i++) {
            assertEquals("[Zombie] Hello this is Zombie " + i, receivedMessages.get(i));
        }
    }

    private List<String> consumeExpectedMessages(NetworkApp app, int expectedMessageCount) {
        List<String> receivedMessages = new ArrayList<>();
        Counter counter = new Counter();

        app.inputFileReader.consume((msg) -> {
            logger.info("<<< Msg: " + msg);
            receivedMessages.add(msg);
            counter.increase();
            if (counter.value() == expectedMessageCount)
                app.inputFileReader.closeStream();
        });
        return receivedMessages;
    }

    private void waitForSubscriptionQueueToAppear(String rabbitMgmtHost) {
        try {
            String queues = "";
            int retryAttempt = 0;
            while (queues.length() <= 2 && ++retryAttempt <= 20) {
                logger.info("Querying rabbit management tool about queues: Attempt: {}.", retryAttempt);
                queues = getQueues(rabbitMgmtHost);
                Thread.sleep(100);
            }
            if (queues.length() <= 2)
                logger.error("No queue found.");
            else
                logger.info("Subscription established. We can now publish messages to exchange. Queue: " + queues);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String getQueues(String host) throws IOException {
        String urlTxt = "http://" + host + "/api/queues";
        URL url = new URL(urlTxt);
        URLConnection uc = url.openConnection();

        String userpass = "guest:guest";
        String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());

        uc.setRequestProperty ("Authorization", basicAuth);

        String result = IOUtils.toString(uc.getInputStream(), StandardCharsets.UTF_8);
        uc.getInputStream().close();

        return result;
    }

    @Test
    @Disabled
    public void testGetQueues() throws IOException {
        logger.info("Queues: " + getQueues("172.19.0.2:15672"));
    }
}

