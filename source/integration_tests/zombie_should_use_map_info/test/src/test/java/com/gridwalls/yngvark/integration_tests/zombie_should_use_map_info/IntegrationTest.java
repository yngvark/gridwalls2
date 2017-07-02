package com.gridwalls.yngvark.integration_tests.zombie_should_use_map_info;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;


public class IntegrationTest {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void zombie_should_use_map_info() throws Exception {
        // Given
        TestApp testApp = TestAppFactory.create();

        testApp.outputFileWriter.write("/subscribeTo Zombie");
        BlockingQueue<String> messages = new LinkedBlockingQueue<>();

        ExecutorService executorService = Executors.newCachedThreadPool();
        Gson gson = new Gson();

        // When
        Future consumeFuture = executorService.submit(() -> {
            try {
                testApp.inputFileReader.consume((String msg) -> {
                    try {
                        messages.put(msg);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // Then
        assertTimeoutPreemptively(Duration.ofSeconds(40), () -> deserialize(gson, messages.take()));
        assertTimeoutPreemptively(Duration.ofMillis(1200), () -> deserialize(gson, messages.take()));
        assertTimeoutPreemptively(Duration.ofMillis(1200), () -> deserialize(gson, messages.take()));

        testApp.inputFileReader.closeStream();
        testApp.outputFileWriter.closeStream();
        consumeFuture.get(500, TimeUnit.MILLISECONDS);
    }

    private void deserialize(Gson gson, String moveMsg) {
        String[] parts = StringUtils.split(moveMsg, " ", 2);
        gson.fromJson(parts[1], Move.class);
    }
}
