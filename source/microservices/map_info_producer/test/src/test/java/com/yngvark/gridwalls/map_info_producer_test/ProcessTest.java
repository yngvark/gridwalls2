package com.yngvark.gridwalls.map_info_producer_test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yngvark.named_piped_app_runner.NamedPipeProcess;
import com.yngvark.named_piped_app_runner.NamedPipeProcessStarter;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.slf4j.LoggerFactory.getLogger;

public class ProcessTest {
    public final Logger logger = getLogger(ProcessTest.class);

    @Test
    public void should_subscribe_to_requeust_topic() throws Exception {
        // Given
        NamedPipeProcess app = NamedPipeProcessStarter.start();

        // When
        List<String> msgs = assertTimeoutPreemptively(Duration.ofSeconds(1), () ->
                consumeExpectedMessages(app, 1)
        );

        // Then
        assertEquals("/subscribeTo MapInfoRequests", msgs.get(0));
    }

    private List<String> consumeExpectedMessages(NamedPipeProcess app, int expectedMessageCount) {
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

    // TODO should_reply_responses_to_given_reply_queue
    @Test
    public void should_reply_map_info_to_reply_queue() throws Exception {
        // Given
        NamedPipeProcess app = NamedPipeProcessStarter.start();

        BlockingQueue<String> blockingQueue = new LinkedBlockingQueue<>();
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(() -> {
            try {
                app.inputFileReader.consume((msg) -> {
                    try {
                        blockingQueue.put(msg);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        String subscribe = assertTimeoutPreemptively(Duration.ofSeconds(1), blockingQueue::take);
        assertEquals("/subscribeTo MapInfoRequests", subscribe);

        Request request = new Request().replyToTopic("TestTopic");
        String requestSerialized = new Gson().toJson(request);

        // When
        app.outputFileWriter.write("[MapInfoRequests] " + requestSerialized);

        // Then
        MapInfo expected = new MapInfo(10, 10);
        String expectedMapInfo = new Gson().toJson(expected);

        String publish = assertTimeoutPreemptively(Duration.ofMillis(500l), blockingQueue::take);
        assertEquals("/publishTo TestTopic " + expectedMapInfo, publish);
    }

    @Test
    public void should_send_map_info() throws Exception {
        // Given
        NamedPipeProcess app = NamedPipeProcessStarter.start();

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

    @Test
    public void should_publish_map_info_to_requested_queue() throws Exception {
        // Given
        NamedPipeProcess app = NamedPipeProcessStarter.start();

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
}

