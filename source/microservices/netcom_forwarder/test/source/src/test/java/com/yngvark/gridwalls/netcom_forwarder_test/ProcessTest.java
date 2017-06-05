package com.yngvark.gridwalls.netcom_forwarder_test;

import com.yngvark.communicate_through_named_pipes.input.InputFileOpener;
import com.yngvark.communicate_through_named_pipes.input.InputFileReader;
import com.yngvark.communicate_through_named_pipes.output.OutputFileOpener;
import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import com.yngvark.gridwalls.netcom_forwarder_test.lib.InputStreamListener;
import com.yngvark.gridwalls.netcom_forwarder_test.lib.ProcessKiller;
import com.yngvark.gridwalls.netcom_forwarder_test.lib.ProcessStarter;
import com.yngvark.gridwalls.rabbitmq.RabbitBrokerConnecter;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.slf4j.LoggerFactory.getLogger;

public class ProcessTest {
    public final Logger logger = getLogger(ProcessTest.class);

    public App startApp() throws Exception {
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

        InputStreamListener stdoutListener = new InputStreamListener();
        stdoutListener.listenInNewThreadOn(process.getInputStream());

        InputStreamListener stderrListener = new InputStreamListener();
        stderrListener.listenInNewThreadOn(process.getErrorStream());

        InputFileOpener inputFileOpener = new InputFileOpener(from);
        OutputFileOpener outputFileOpener = new OutputFileOpener(to);

        logger.info("Opening input.");
        InputFileReader inputFileReader = inputFileOpener.openStream(() -> Thread.sleep(3000));
        logger.info("Opening output.");
        OutputFileWriter outputFileWriter = outputFileOpener.openStream(() -> Thread.sleep(3000));
        logger.info("Streams opened.");

        App app = new App();
        app.host = host;
        app.rabbitConnection = rabbitConnection;
        app.process = process;
        app.stdoutListener = stdoutListener;
        app.stderrListener = stderrListener;
        app.inputFileReader = inputFileReader;
        app.outputFileWriter = outputFileWriter;
        return app;
    }

    class App {
        String host;
        RabbitConnection rabbitConnection;
        Process process;
        InputStreamListener stdoutListener;
        InputStreamListener stderrListener;
        InputFileReader inputFileReader;
        OutputFileWriter outputFileWriter;

        public void stopAndFreeResources() throws Exception {
            stdoutListener.stopListening();
            stderrListener.stopListening();

            inputFileReader.closeStream();
            outputFileWriter.closeStream();

            rabbitConnection.disconnectIfConnected();

            ProcessKiller.killUnixProcess(process);
            ProcessKiller.waitForExitAndAssertExited(process, 5, TimeUnit.SECONDS);
        }
    }

    @Test
    public void published_msgs_should_be_sent_to_network() throws Exception {
        // Given
        App app = startApp();
        app.outputFileWriter.write("/myNameIs netcomForwarderTest");

        int messagesToSend = 3;
        RabbitSubscriber rabbitSubscriber = new RabbitSubscriber(app.rabbitConnection);

        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<List<String>> receivePublishedMessagesFuture = executorService.submit(() ->
            subscribeAndReceiveMessages(messagesToSend, rabbitSubscriber)
        );
        waitForSubscriptionQueueToAppear(app.host + ":15672");

        // When
        for (int i = 0; i < messagesToSend; i++) {
            app.outputFileWriter.write("/publish Hi this is networkforwarderTest: i=" + i);
        }

        // Record result
        logger.info("Waiting for our subscriber to receive published messages.");
        List<String> recordedNetworMessages = receivePublishedMessagesFuture.get(6, TimeUnit.DAYS);

        // Cleanup
        logger.info("Closing output writer, which should make app stop by itself.");
        app.outputFileWriter.closeStream();
        app.stopAndFreeResources();

        // Then
        assertEquals(3, recordedNetworMessages.size());
        assertEquals("Hi this is networkforwarderTest: i=0", recordedNetworMessages.get(0));
        assertEquals("Hi this is networkforwarderTest: i=1", recordedNetworMessages.get(1));
        assertEquals("Hi this is networkforwarderTest: i=2", recordedNetworMessages.get(2));
    }

    private List<String> subscribeAndReceiveMessages(int messageCountToExpect, RabbitSubscriber rabbitSubscriber) {
        List<String> recordedNetworMessages = new ArrayList<>();
        Lock lock = new Lock();
        Counter counter = new Counter();

        rabbitSubscriber.subscribe("networkForwarderTestReader", "netcomForwarderTest", (msg) -> {
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
    public void we_should_receive_messages_from_queue_we_subscribe_to() throws Exception {
        // Given
        App app = startApp();
        app.outputFileWriter.write("/myNameIs netcomForwarderTest");
        app.outputFileWriter.write("/subscribeTo zombie");
        waitForSubscriptionQueueToAppear(app.host + ":15672");

        // When
        logger.info("Publishing to zombie.");
        RabbitPublisher rabbitPublisher = new RabbitPublisher(app.rabbitConnection);
        int messagesToSend = 3;
        for (int i = 0; i < messagesToSend; i++) {
            rabbitPublisher.publish("zombie", "Hello this is Zombie " + i);
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
            assertEquals("[zombie] Hello this is Zombie " + i, receivedMessages.get(i));
        }
    }

    private List<String> consumeExpectedMessages(App app, int expectedMessageCount) {
        List<String> receivedMessages = new ArrayList<>();
        Counter counter = new Counter();

        try {
            app.inputFileReader.consume((msg) -> {
                logger.info("<<< Msg: " + msg);
                receivedMessages.add(msg);
                counter.increase();
                if (counter.value() == expectedMessageCount)
                    app.inputFileReader.closeStream();
            });
            return receivedMessages;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

