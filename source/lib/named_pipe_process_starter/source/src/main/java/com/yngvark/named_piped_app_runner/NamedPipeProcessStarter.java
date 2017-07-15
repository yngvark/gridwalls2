package com.yngvark.named_piped_app_runner;

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

public class NamedPipeProcessStarter {
    public static final Logger logger = getLogger(NamedPipeProcessStarter.class);
    private static NamedPipeProcess namedPipeProcess = new NamedPipeProcess();

    public static NamedPipeProcess start() throws Exception {
        String toAbsolutePath = createFifo("build/fifo_to_microservice");
        String fromAbsolutePath = createFifo("build/fifo_from_microservice");
        startProcess(toAbsolutePath, fromAbsolutePath);
        openFifos(toAbsolutePath, fromAbsolutePath);

        return namedPipeProcess;
    }

    public static String createFifo(String filename) {
        Path file = Paths.get(filename);
        if (Files.exists(file)) {
            try {
                Files.delete(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            Runtime.getRuntime().exec("mkfifo " + filename).waitFor();
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
        String absolutePath = Paths.get(filename).toAbsolutePath().toString();
        logger.info("Fifo: {}", absolutePath);
        return absolutePath;
    }

    private static void startProcess(String toAbsolutePath, String fromAbsolutePath) {
        logger.info("Current directory: {}", Paths.get("").toAbsolutePath());
        Process process = null;
        try {
            process = ProcessStarter.startProcess(
                    "../source/build/install/app/bin/run",
                    toAbsolutePath,
                    fromAbsolutePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        namedPipeProcess.process = process;

        InputStreamListener stdoutListener = new InputStreamListener();
        stdoutListener.listenInNewThreadOn(process.getInputStream());
        namedPipeProcess.stdoutListener = stdoutListener;

        InputStreamListener stderrListener = new InputStreamListener();
        stderrListener.listenInNewThreadOn(process.getErrorStream());
        namedPipeProcess.stderrListener = stderrListener;
    }

    private static void openFifos(String toAbsolutePath, String fromAbsolutePath) {
        InputFileOpener inputFileOpener = new InputFileOpener(fromAbsolutePath);
        OutputFileOpener outputFileOpener = new OutputFileOpener(toAbsolutePath);

        logger.info("Opening output.");
        OutputFileWriter outputFileWriter = outputFileOpener.openStream(() -> Thread.sleep(3000));
        logger.info("Opening input.");
        InputFileReader inputFileReader = inputFileOpener.openStream(() -> Thread.sleep(3000));
        logger.info("Streams opened.");

        namedPipeProcess.inputFileReader = inputFileReader;
        namedPipeProcess.outputFileWriter = outputFileWriter;
    }

    public static NamedPipeProcess initFifos() throws Exception {
        String toAbsolutePath = createFifo("build/fifo_to_microservice");
        String fromAbsolutePath = createFifo("build/fifo_from_microservice");
        openFifos(toAbsolutePath, fromAbsolutePath);

        return namedPipeProcess;
    }

}
