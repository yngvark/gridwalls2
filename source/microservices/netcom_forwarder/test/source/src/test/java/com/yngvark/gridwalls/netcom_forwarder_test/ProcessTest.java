package com.yngvark.gridwalls.netcom_forwarder_test;

import com.yngvark.communicate_through_named_pipes.input.InputFileOpener;
import com.yngvark.communicate_through_named_pipes.input.InputFileReader;
import com.yngvark.communicate_through_named_pipes.output.OutputFileOpener;
import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import com.yngvark.gridwalls.netcom_forwarder_test.lib.InputStreamListener;
import com.yngvark.gridwalls.netcom_forwarder_test.lib.ProcessStarter;
import com.yngvark.gridwalls.netcom_forwarder_test.rabbitmq.RabbitBrokerConnecter;
import com.yngvark.gridwalls.netcom_forwarder_test.rabbitmq.RabbitConnection;
import com.yngvark.gridwalls.netcom_forwarder_test.rabbitmq.RabbitPublisher;
import com.yngvark.gridwalls.netcom_forwarder_test.rabbitmq.RabbitSubscriber;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.slf4j.LoggerFactory.getLogger;

public class ProcessTest {
    public final Logger logger = getLogger(ProcessTest.class);

    @Test
    public void published_msgs_should_be_sent_to_network() throws Exception {
        // Given
        logger.info(Paths.get(".").toAbsolutePath().toString());
        String host = "172.19.0.2";
        RabbitBrokerConnecter rabbitBrokerConnecter = new RabbitBrokerConnecter(host);
        RabbitConnection rabbitConnection = rabbitBrokerConnecter.connect();

        String to = "build/to_netcom_forwarder";
        String from = "build/from_netcom_forwarder";

        Path toPath = Paths.get(from);
        if (Files.exists(toPath)) {
            Files.delete(toPath);
        }
        Path fromPath = Paths.get(from);
        if (Files.exists(fromPath)) {
            Files.delete(fromPath);
        }
        Runtime.getRuntime().exec("mkfifo " + to).waitFor();
        Runtime.getRuntime().exec("mkfifo " + from).waitFor();

        Process process = ProcessStarter.startProcess(
                "../../source/build/install/app/bin/run",
                to,
                from,
                host);

        InputStreamListener inputStreamListener = new InputStreamListener();
        inputStreamListener.listenInNewThreadOn(process.getInputStream());

        InputStreamListener stderrListener = new InputStreamListener();
        stderrListener.listenInNewThreadOn(process.getErrorStream());

        InputFileOpener inputFileOpener = new InputFileOpener(from);
        OutputFileOpener outputFileOpener = new OutputFileOpener(to);

        logger.info("Opening input.");
        InputFileReader inputFileReader = inputFileOpener.openStream(() -> Thread.sleep(3000));
        logger.info("Opening output.");
        OutputFileWriter outputFileWriter = outputFileOpener.openStream(() -> Thread.sleep(3000));
        logger.info("Streams opened.");

        // When
        outputFileWriter.write("/myNameIs netcomForwarderTest");

        int messagesToSend = 3;

        RabbitSubscriber rabbitSubscriber = new RabbitSubscriber(rabbitConnection);
        List<String> recordedNetworMessages = new ArrayList<>();

        ExecutorService executorService = Executors.newCachedThreadPool();
        Future receivePublishedMessagesFuture = executorService.submit(() -> {
            BlockingQueue blockingQueue = new LinkedBlockingQueue();
            Map<String, Integer> counter = new HashMap<>();
            counter.put("receivedMessageCount", 0);
            rabbitSubscriber.subscribe("networkForwarderTestReader", "netcomForwarderTest", (msg) -> {

                logger.info("<<< Networkmsg: " + msg);
                recordedNetworMessages.add(msg);

                int currentCount = counter.get("receivedMessageCount") + 1;
                counter.put("receivedMessageCount", currentCount);
                if (currentCount == messagesToSend) {
                    try {
                        blockingQueue.put("We have now received expected messages. Continue test.");
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            try {
                blockingQueue.take();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        waitForAnyQueueToAppear(host + ":15672");

        // When
        for (int i = 0; i < messagesToSend; i++) {
            outputFileWriter.write("/publish Hi this is networkforwarderTest: i=" + i);
        }

        // Cleanup

        logger.info("Waiting for our subscriber to receive published messages.");
        receivePublishedMessagesFuture.get(6, TimeUnit.DAYS);

        logger.info("Closing output writer.");
        outputFileWriter.closeStream();

        logger.info("Reading");
        inputFileReader.consume((msg) -> {
            logger.info("<<< Msg: " + msg);
        });

        // Then
        logger.info("Killing process.");

        // Finally
        logger.info("Stopping listening to inpustreamListener");
        inputStreamListener.stopListening();

        logger.info("Closing writer");
        inputFileReader.closeStream();
        logger.info("Closing reader");
        outputFileWriter.closeStream();
        logger.info("Disconnecting.");

        rabbitConnection.disconnectIfConnected();

        // Then
        assertEquals(3, recordedNetworMessages.size());
        assertEquals("Hi this is networkforwarderTest: i=0", recordedNetworMessages.get(0));
        assertEquals("Hi this is networkforwarderTest: i=1", recordedNetworMessages.get(1));
        assertEquals("Hi this is networkforwarderTest: i=2", recordedNetworMessages.get(2));
    }

    @Test
    public void we_should_receive_messages_from_queue_we_subscribe_to() throws Exception {
        // Given
        logger.info(Paths.get(".").toAbsolutePath().toString());
        String host = "172.19.0.2";
        RabbitBrokerConnecter rabbitBrokerConnecter = new RabbitBrokerConnecter(host);
        RabbitConnection rabbitConnection = rabbitBrokerConnecter.connect();

        String to = "build/to_netcom_forwarder";
        String from = "build/from_netcom_forwarder";

        Path toPath = Paths.get(from);
        if (Files.exists(toPath)) {
            Files.delete(toPath);
        }
        Path fromPath = Paths.get(from);
        if (Files.exists(fromPath)) {
            Files.delete(fromPath);
        }
        Runtime.getRuntime().exec("mkfifo " + to).waitFor();
        Runtime.getRuntime().exec("mkfifo " + from).waitFor();

        Process process = ProcessStarter.startProcess(
                "../../source/build/install/app/bin/run",
                to,
                from,
                host);

        InputStreamListener inputStreamListener = new InputStreamListener();
        inputStreamListener.listenInNewThreadOn(process.getInputStream());

        InputStreamListener stderrListener = new InputStreamListener();
        stderrListener.listenInNewThreadOn(process.getErrorStream());

        InputFileOpener inputFileOpener = new InputFileOpener(from);
        OutputFileOpener outputFileOpener = new OutputFileOpener(to);

        logger.info("Opening input.");
        InputFileReader inputFileReader = inputFileOpener.openStream(() -> Thread.sleep(3000));
        logger.info("Opening output.");
        OutputFileWriter outputFileWriter = outputFileOpener.openStream(() -> Thread.sleep(3000));
        logger.info("Streams opened.");

        outputFileWriter.write("/myNameIs netcomForwarderTest");

        logger.info("Subscribing to zombie.");
        outputFileWriter.write("/subscribeTo zombie");
        waitForAnyQueueToAppear(host + ":15672");

        // When
        logger.info("Publishing to zombie.");
        RabbitPublisher rabbitPublisher = new RabbitPublisher(rabbitConnection);
        int messagesToSend = 3;
        for (int i = 0; i < messagesToSend; i++) {
            rabbitPublisher.publish("zombie", "Hello this is Zombie " + i);
        }

        // Capture published messages...
        List<String> receivedMessages = new ArrayList<>();
        ExecutorService executorService = Executors.newCachedThreadPool();

        Future consumeExpectedMessagesFuture = executorService.submit(() -> {
            try {
                Map<String, Integer> counter = new HashMap<>();
                counter.put("receivedMessageCount", 0);
                inputFileReader.consume((msg) -> {
                    logger.info("<<< Msg: " + msg);
                    receivedMessages.add(msg);
                    int currentCount = counter.get("receivedMessageCount") + 1;
                    counter.put("receivedMessageCount", currentCount);
                    if (currentCount == messagesToSend)
                        inputFileReader.closeStream();
                });
                return;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        logger.info("Starting read future");
        consumeExpectedMessagesFuture.get(3, TimeUnit.SECONDS);

        // Clean up...

        logger.info("Closing output writer.");
        outputFileWriter.closeStream();

        logger.info("Stopping listening to inpustreamListener");
        inputStreamListener.stopListening();

        logger.info("Closing reader");
        inputFileReader.closeStream();
        logger.info("Closing writer");
        outputFileWriter.closeStream();
        logger.info("Disconnecting.");

        rabbitConnection.disconnectIfConnected();

        // Then
        assertEquals(messagesToSend, receivedMessages.size());
        for (int i = 0; i < messagesToSend; i++) {
            assertEquals("[zombie] Hello this is Zombie " + i, receivedMessages.get(i));
        }
    }

    private void waitForAnyQueueToAppear(String rabbitMgmtHost) {
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

