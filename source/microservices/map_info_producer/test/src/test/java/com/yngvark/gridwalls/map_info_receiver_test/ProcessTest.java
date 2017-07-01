package com.yngvark.gridwalls.map_info_receiver_test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yngvark.process_test_helper.TestableApp;
import com.yngvark.process_test_helper.TestableAppFactory;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.io.IOException;
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

    @Test
    public void should_send_map_info() throws Exception {
        // Given
        TestableApp app = TestableAppFactory.start();

        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<List<String>> consumeExpectedMessagesFuture = executorService.submit(() ->
                consumeExpectedMessages(app, 1)
        );

        // When
        List<String> messages = consumeExpectedMessagesFuture.get(1, TimeUnit.SECONDS);

        // Then
        String msg = messages.get(0);
        String[] parts = StringUtils.split(msg, " ", 3);

        assertEquals("/publishTo", parts[0]);
        assertEquals("MapInfo", parts[1]);

        Gson gson = new GsonBuilder().create();
        gson.fromJson(parts[2], MapInfo.class);

        // Finally
        app.stop();
    }

    private List<String> consumeExpectedMessages(TestableApp app, int expectedMessageCount) {
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

