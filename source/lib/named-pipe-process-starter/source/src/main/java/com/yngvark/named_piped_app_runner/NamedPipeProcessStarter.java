package com.yngvark.named_piped_app_runner;

import com.yngvark.communicate_through_named_pipes.input.InputFileLineReader;
import com.yngvark.communicate_through_named_pipes.input.InputFileOpener;
import com.yngvark.communicate_through_named_pipes.output.OutputFileOpener;
import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.slf4j.LoggerFactory.getLogger;

public class NamedPipeProcessStarter {
    public static final Logger logger = getLogger(NamedPipeProcessStarter.class);
    private static NamedPipeProcess namedPipeProcess = new NamedPipeProcess();

    public static NamedPipeProcess start() throws Exception {
        return start("");
    }

    public static NamedPipeProcess start(String... args) throws Exception {
        String toAbsolutePath = createFifo("build/fifo_to_microservice");
        String fromAbsolutePath = createFifo("build/fifo_from_microservice");
        startProcess(toAbsolutePath, fromAbsolutePath, args);
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

    private static void startProcess(String toAbsolutePath, String fromAbsolutePath, String... args) {
        logger.info("Current directory: {}", Paths.get("").toAbsolutePath());
        Process process;
        try {
            String[] path = mergeStringsWithArray(toAbsolutePath, fromAbsolutePath, args);
            process = ProcessStarter.startProcess(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        namedPipeProcess.process = process;

        InputStreamListener stdoutListener = new InputStreamListener("stdoutListener");
        stdoutListener.listenInNewThreadOn(process.getInputStream());
        namedPipeProcess.stdoutListener = stdoutListener;

        InputStreamListener stderrListener = new InputStreamListener("stderrListener");
        stderrListener.listenInNewThreadOn(process.getErrorStream());
        namedPipeProcess.stderrListener = stderrListener;
    }

    private static String[] mergeStringsWithArray(String toAbsolutePath, String fromAbsolutePath, String[] args) {
        return ArrayUtils.addAll(
                        new String[] {
                                "../source/build/install/app/bin/run",
                                toAbsolutePath,
                                fromAbsolutePath
                        },
                        args
                );
    }

    private static void openFifos(String toAbsolutePath, String fromAbsolutePath) {
        InputFileOpener inputFileOpener = new InputFileOpener(fromAbsolutePath);
        OutputFileOpener outputFileOpener = new OutputFileOpener(toAbsolutePath);

        OutputFileWriter outputFileWriter = outputFileOpener.openStream(() -> Thread.sleep(3000));
        InputFileLineReader inputFileLineReader = inputFileOpener.openLineStream(() -> Thread.sleep(3000));
        logger.info("Both output and input streams opened.");

        namedPipeProcess.inputFileLineReader = inputFileLineReader;
        namedPipeProcess.outputFileWriter = outputFileWriter;
    }

    public static NamedPipeProcess initFifos() throws Exception {
        String toAbsolutePath = createFifo("build/fifo_to_microservice");
        String fromAbsolutePath = createFifo("build/fifo_from_microservice");
        openFifos(toAbsolutePath, fromAbsolutePath);

        return namedPipeProcess;
    }

}
