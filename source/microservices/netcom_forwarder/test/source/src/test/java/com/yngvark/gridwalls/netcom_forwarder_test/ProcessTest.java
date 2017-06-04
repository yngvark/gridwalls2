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
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.slf4j.LoggerFactory.getLogger;

public class ProcessTest {
    public final Logger logger = getLogger(ProcessTest.class);

    @Test
    public void normal_run() throws IOException, InterruptedException, NoSuchFieldException, IllegalAccessException {
        // Given
        logger.info(Paths.get(".").toAbsolutePath().toString());

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
                "172.19.0.2");

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
        outputFileWriter.write("/subscribeTo ServerMessages");

        for (int i = 0; i < 3; i++) {
            outputFileWriter.write("/publish Hi this is networkforwarderTest: i=" + i);
        }

        logger.info("Closing stream. - - - -- - -");
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
    }

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

        for (int i = 0; i < 3; i++) {
            outputFileWriter.write("/publish Hi this is networkforwarderTest: i=" + i);
        }

        // Then
        RabbitSubscriber rabbitSubscriber = new RabbitSubscriber(rabbitConnection);
        List<String> recordedNetworMessages = new ArrayList<>();

        rabbitSubscriber.subscribe("networkForwarderTestReader", "netcomForwarderTest", (msg) -> {
            logger.info("< < < Networkmsg: " + msg);
            recordedNetworMessages.add(msg);
        });

        logger.info("Closing stream. - - - -- - -");
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
    public void we_should_receive_messages_from_subscribed_queue() throws Exception {
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

        // When
        logger.info("Publishing to zombie.");
        RabbitPublisher rabbitPublisher = new RabbitPublisher(rabbitConnection);

        logger.info("Subscribing to zombie.");
        outputFileWriter.write("/subscribeTo zombie");

        Thread.sleep(4000l); // TODO avoid this stuff. Race condition here, just try to set it to 0.
        // TODO må finne ut av dette..

        rabbitPublisher.publish("zombie", "Hello this is Zombie 1");
        rabbitPublisher.publish("zombie", "Hello this is Zombie 2");
        rabbitPublisher.publish("zombie", "Hello this is Zombie 3");

        logger.info("Closing stream. - - - -- - -");
        outputFileWriter.closeStream();

        logger.info("Reading");
        List<String> receivedMessages = new ArrayList<>();
        inputFileReader.consume((msg) -> {
            logger.info("<<< Msg: " + msg);
            receivedMessages.add(msg);
        });

        // Cleanup
        logger.info("Stopping listening to inpustreamListener");
        inputStreamListener.stopListening();

        logger.info("Closing writer");
        inputFileReader.closeStream();
        logger.info("Closing reader");
        outputFileWriter.closeStream();
        logger.info("Disconnecting.");

        rabbitConnection.disconnectIfConnected();

        // Then
        assertEquals(3, receivedMessages.size());
        assertEquals("[zombie] Hello this is Zombie 1", receivedMessages.get(0));
        assertEquals("[zombie] Hello this is Zombie 2", receivedMessages.get(1));
        assertEquals("[zombie] Hello this is Zombie 3", receivedMessages.get(2));
    }
}

