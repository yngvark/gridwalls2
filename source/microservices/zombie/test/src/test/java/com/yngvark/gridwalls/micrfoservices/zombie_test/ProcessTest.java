package com.yngvark.gridwalls.micrfoservices.zombie_test;

import com.yngvark.process_test_helper.TestableApp;
import com.yngvark.process_test_helper.TestableAppFactory;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.io.IOException;
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
    public void can_read_message_from_process() throws Exception {
        // Given
        TestableApp app = TestableAppFactory.start();

        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<List<String>> consumeExpectedMessagesFuture = executorService.submit(() ->
                consumeExpectedMessages(app, 1)
        );

        // When
        List<String> messages = consumeExpectedMessagesFuture.get(1, TimeUnit.SECONDS);

        // Then
        assertTrue(messages.get(0).length() > 0);

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

    @Test
    public void should
}

