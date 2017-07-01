package com.yngvark.gridwalls.micrfoservices.zombie_test;

import com.yngvark.communicate_through_named_pipes.input.InputFileOpener;
import com.yngvark.communicate_through_named_pipes.input.InputFileReader;
import com.yngvark.communicate_through_named_pipes.output.OutputFileOpener;
import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import com.yngvark.process_test_helper.InputStreamListener;
import com.yngvark.process_test_helper.ProcessKiller;
import com.yngvark.process_test_helper.ProcessStarter;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.io.IOException;
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

public class ProcessTest {
    public final Logger logger = getLogger(ProcessTest.class);

    public App startApp() throws Exception {
        String to = "build/to_microservice";
        String from = "build/from_microservice";

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

        String toAbsolutePath = Paths.get(to).toAbsolutePath().toString();
        String fromAbsolutePath = Paths.get(from).toAbsolutePath().toString();

        logger.info("Current directory: {}", Paths.get("").toAbsolutePath());
        logger.info("To: {}", toAbsolutePath);
        logger.info("From: {}", fromAbsolutePath);

        Process process = ProcessStarter.startProcess(
                "../source/build/install/app/bin/run",
                toAbsolutePath,
                fromAbsolutePath);

        InputStreamListener stdoutListener = new InputStreamListener();
        stdoutListener.listenInNewThreadOn(process.getInputStream());

        InputStreamListener stderrListener = new InputStreamListener();
        stderrListener.listenInNewThreadOn(process.getErrorStream());

        InputFileOpener inputFileOpener = new InputFileOpener(from);
        OutputFileOpener outputFileOpener = new OutputFileOpener(to);

        logger.info("Opening output.");
        OutputFileWriter outputFileWriter = outputFileOpener.openStream(() -> Thread.sleep(3000));
        logger.info("Opening input.");
        InputFileReader inputFileReader = inputFileOpener.openStream(() -> Thread.sleep(3000));
        logger.info("Streams opened.");

        App app = new App();
        app.process = process;
        app.stdoutListener = stdoutListener;
        app.stderrListener = stderrListener;
        app.inputFileReader = inputFileReader;
        app.outputFileWriter = outputFileWriter;
        return app;
    }

    class App {
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

            ProcessKiller.killUnixProcess(process);
            ProcessKiller.waitForExitAndAssertExited(process, 5, TimeUnit.SECONDS);
        }
    }

    @Test
    public void can_read_message_from_process() throws Exception {
        // Given
        App app = startApp();

        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<List<String>> consumeExpectedMessagesFuture = executorService.submit(() ->
                consumeExpectedMessages(app, 1)
        );

        // When
        List<String> messages = consumeExpectedMessagesFuture.get(1, TimeUnit.SECONDS);

        // Then
        assertTrue(messages.get(0).length() > 0);

        // Finally
        app.stopAndFreeResources();
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
}

