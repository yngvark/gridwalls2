package com.yngvark.process_test_helper;

import com.yngvark.communicate_through_named_pipes.input.InputFileOpener;
import com.yngvark.communicate_through_named_pipes.input.InputFileReader;
import com.yngvark.communicate_through_named_pipes.output.OutputFileOpener;
import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.slf4j.LoggerFactory.getLogger;

public class TestableAppFactory {
    public static final Logger logger = getLogger(TestableAppFactory.class);
    private static TestableApp testableApp = new TestableApp();

    public static TestableApp start() throws Exception {
        String toAbsolutePath = createFifo("build/fifo_to_microservice");
        String fromAbsolutePath = createFifo("build/fifo_from_microservice");
        startProcess(toAbsolutePath, fromAbsolutePath);
        openFifos(toAbsolutePath, fromAbsolutePath);

        return testableApp;
    }

    public static String createFifo(String filename) throws IOException, InterruptedException {
        Path file = Paths.get(filename);
        if (Files.exists(file)) {
            Files.delete(file);
        }
        Runtime.getRuntime().exec("mkfifo " + filename).waitFor();
        String absolutePath = Paths.get(filename).toAbsolutePath().toString();
        logger.info("Fifo: {}", absolutePath);
        return absolutePath;
    }

    private static void startProcess(String toAbsolutePath, String fromAbsolutePath) throws IOException {
        logger.info("Current directory: {}", Paths.get("").toAbsolutePath());
        Process process = ProcessStarter.startProcess(
                "../source/build/install/app/bin/run",
                toAbsolutePath,
                fromAbsolutePath);
        testableApp.process = process;

        InputStreamListener stdoutListener = new InputStreamListener();
        stdoutListener.listenInNewThreadOn(process.getInputStream());
        testableApp.stdoutListener = stdoutListener;

        InputStreamListener stderrListener = new InputStreamListener();
        stderrListener.listenInNewThreadOn(process.getErrorStream());
        testableApp.stderrListener = stderrListener;
    }

    private static void openFifos(String toAbsolutePath, String fromAbsolutePath) throws IOException {
        InputFileOpener inputFileOpener = new InputFileOpener(fromAbsolutePath);
        OutputFileOpener outputFileOpener = new OutputFileOpener(toAbsolutePath);

        logger.info("Opening output.");
        OutputFileWriter outputFileWriter = outputFileOpener.openStream(() -> Thread.sleep(3000));
        logger.info("Opening input.");
        InputFileReader inputFileReader = inputFileOpener.openStream(() -> Thread.sleep(3000));
        logger.info("Streams opened.");

        testableApp.inputFileReader = inputFileReader;
        testableApp.outputFileWriter = outputFileWriter;
    }


    public static TestableApp initFifos() throws Exception {
        String toAbsolutePath = createFifo("build/fifo_to_microservice");
        String fromAbsolutePath = createFifo("build/fifo_from_microservice");
        openFifos(toAbsolutePath, fromAbsolutePath);

        return testableApp;
    }

}
