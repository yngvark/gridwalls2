package com.yngvark.gridwalls.map_info_producer_test;

import com.google.gson.Gson;
import com.yngvark.named_piped_app_runner.NamedPipeProcess;
import com.yngvark.named_piped_app_runner.NamedPipeProcessStarter;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.*;
import static org.slf4j.LoggerFactory.getLogger;

public class ProcessTest {
    public final Logger logger = getLogger(ProcessTest.class);

    @Test
    public void should_subscribe_to_requeusts_topic() throws Exception {
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

        app.inputFileReader.consume((msg) -> {
            logger.info("<<< Msg: " + msg);
            receivedMessages.add(msg);
            counter.increase();
            if (counter.value() == expectedMessageCount)
                app.inputFileReader.closeStream();
        });
        return receivedMessages;
    }

    @Test
    public void should_reply_map_info_to_reply_topic() throws Exception {
        // Given
        NamedPipeProcess app = NamedPipeProcessStarter.start();

        BlockingQueue<String> blockingQueue = new LinkedBlockingQueue<>();
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(() -> {
            app.inputFileReader.consume((msg) -> {
                try {
                    blockingQueue.put(msg);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        });

        String subscribe = assertTimeoutPreemptively(Duration.ofSeconds(1), blockingQueue::take);
        assertEquals("/subscribeTo MapInfoRequests", subscribe);

        MapInfoRequest request = new MapInfoRequest().replyToTopic("TestReplyTopic");
        String requestSerialized = new Gson().toJson(request);

        // When
        app.outputFileWriter.write("[MapInfoRequests] " + requestSerialized);

        // Then
        MapInfo expected = new MapInfo(15, 10);
        String expectedMapInfo = new Gson().toJson(expected);

        String publish = assertTimeoutPreemptively(Duration.ofMillis(500l), blockingQueue::take);
        assertEquals("/publishTo TestReplyTopic " + expectedMapInfo, publish);
    }
}

