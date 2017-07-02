package com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs.get_map_info;

import com.yngvark.gridwalls.microservices.zombie.TestSerializer;
import com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs.NetworkMsgListenerContext;
import com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs.ProducerContext;
import com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs.move.ZombieMoverFactory;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class MapInfoReceiverTest {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    void nextMsg() throws InterruptedException, ExecutionException {
        MapInfoReceiver mapInfoReceiver = new MapInfoReceiver(
                new TestSerializer(),
                mock(ZombieMoverFactory.class));

        ProducerContext producerContext = new ProducerContext(mapInfoReceiver);

        // When
        String msg = producerContext.nextMsg();

        // Then
        assertEquals("/subscribeTo MapInfo", msg);

        // When
        ExecutorService executorService = Executors.newCachedThreadPool();
        Future produceFuture = executorService.submit(() -> mapInfoReceiver.nextMsg(producerContext));
        assertThrows(TimeoutException.class, () -> produceFuture.get(300, TimeUnit.MILLISECONDS));

        NetworkMsgListenerContext consumerContext = new NetworkMsgListenerContext(mapInfoReceiver);
        Future exceptionFuture = executorService.submit(() -> {
            try {
                consumerContext.messageReceived("[MapInfo] {\"height\":15,\"width\":10}\n");
            } catch (Exception e) {
                logger.info("Exception occurred!!!");
                throw new RuntimeException(e);
            }

        });
        Thread.sleep(1000);
//        exceptionFuture.get();
    }

}