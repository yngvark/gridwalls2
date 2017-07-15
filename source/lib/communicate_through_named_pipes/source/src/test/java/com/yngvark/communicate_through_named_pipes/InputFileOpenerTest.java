package com.yngvark.communicate_through_named_pipes;

import com.yngvark.communicate_through_named_pipes.input.InputFileReader;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.slf4j.LoggerFactory.getLogger;

class InputFileOpenerTest {
    private final Logger logger = getLogger(getClass());

    @Test
    void should_retry_if_file_is_not_available() throws IOException, ExecutionException, InterruptedException {
        // Given
        Path buildDir = Paths.get("build");
        String file = "build/toConsume";

        createFileIfNotExists(buildDir, file);

        Map<String, Boolean> testResult = new HashMap<>();

        Map<String, Integer> timeUnitsPassedMap = new ConcurrentHashMap<>();
        timeUnitsPassedMap.put("value", 0);

        RetrySleeper retrySleeper = () -> {
            Integer timeUnitsPassed = timeUnitsPassedMap.get("value") + 1;
            timeUnitsPassedMap.put("value", timeUnitsPassed);

            if (timeUnitsPassed == 20) { // Test assumption: This number hopefully implies forever
                try {
                    testResult.put("indefiniteRetriesAttempted", true);
                    logger.info("Retrying probably works fine now. Let's create the file so our consumer can open it.");
                    new FileOutputStream(file).close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        };
        RetryWaiter retryWaiter = new RetryWaiter(retrySleeper);

        ExecutorService executorService = Executors.newCachedThreadPool();

        // When
        Future future = executorService.submit(() -> {
            retryWaiter.waitUntilFileExists(file);
        });

        // Then
        try {
            future.get(3, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            throw e;
        } catch (TimeoutException e) {
            throw new RuntimeException("Test didn't complete in time", e);
        }

        assertTrue(testResult.getOrDefault("indefiniteRetriesAttempted", false));

        Files.delete(Paths.get(file));
    }

    private void createFileIfNotExists(Path buildDir, String file) throws IOException {
        if (!Files.exists(buildDir)) {
            Files.createDirectories(buildDir);
        }

        if (Files.exists(Paths.get(file))) {
            Files.delete(Paths.get(file));
        }
    }

    @Test // TODO
    public void should_read_one_line() throws IOException {
        // Given
        PipedOutputStream pipedOutputStream = new PipedOutputStream();
        Writer writer = new BufferedWriter(new OutputStreamWriter(pipedOutputStream));

        PipedInputStream pipedInputStream = new PipedInputStream();
        pipedInputStream.connect(pipedOutputStream);
        InputFileReader inputFileReader = new InputFileReader(
                new BufferedReader(new InputStreamReader(pipedInputStream)));

        writer.write("Hei");

        // When
        // String msg = inputFileReader.consumeOne(100, TimeUnit.MILLISECONDS);

        // Then
        // assertEquals("Hei", msg);
    }

}