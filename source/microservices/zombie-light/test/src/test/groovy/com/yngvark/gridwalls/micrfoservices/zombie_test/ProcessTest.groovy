package com.yngvark.gridwalls.micrfoservices.zombie_test

import com.google.gson.Gson
import com.yngvark.gridwalls.microservices.zombie.common.MapInfo
import com.yngvark.gridwalls.microservices.zombie.common.MapInfoRequest
import com.yngvark.named_piped_app_runner.NamedPipeProcess
import com.yngvark.named_piped_app_runner.NamedPipeProcessStarter
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue

class ProcessTest {
    public final Logger logger = LoggerFactory.getLogger(ProcessTest.class)
    private NamedPipeProcess app

    @Test
    void should_exit_when_killing_process() throws Exception {
        // Given
        app = NamedPipeProcessStarter.start("--nosleep -seed=123")
        def gson = new Gson()

        // When
        String subscription = AppLineReader.readLine(app)

        // Then
        logger.debug(subscription)
        assertEquals("/subscribeTo Zombie_MapInfo", subscription)

        String mapInfoRequest = gson.toJson(
                new MapInfoRequest().replyToTopic("Zombie_MapInfo"))
        String publish = AppLineReader.readLine(app)
        assertEquals("/publishTo MapInfoRequests " + mapInfoRequest, publish)

        // And given
        String mapInfo = gson.toJson(new MapInfo(15, 10))
        app.outputFileWriter.write("[Zombie_MapInfo] " + mapInfo)

        for (int i = 0; i < 2; i++) {
            String move = app.inputFileLineReader.readLine()
            logger.info("{}: {}", i, move)
        }

        // When
        Future<Void> appStopfuture = Executors.newCachedThreadPool().submit({
            app.stop()
        })

        // Then
        appStopfuture.get(3, TimeUnit.SECONDS)
    }

    @Test
    void turn_time_should_be_below_50ms_when_using_nosleep() throws Exception {
        // Given
        app = NamedPipeProcessStarter.start("--nosleep -seed=123")
        def gson = new Gson()

        // When
        String subscription = AppLineReader.readLine(app)

        // Then
        logger.debug(subscription)
        assertEquals("/subscribeTo Zombie_MapInfo", subscription)

        String mapInfoRequest = gson.toJson(
                new MapInfoRequest().replyToTopic("Zombie_MapInfo"))
        String publish = AppLineReader.readLine(app)
        assertEquals("/publishTo MapInfoRequests " + mapInfoRequest, publish)

        // And given
        String mapInfo = gson.toJson(new MapInfo(15, 10))
        app.outputFileWriter.write("[Zombie_MapInfo] " + mapInfo)

        LocalDateTime last = LocalDateTime.now()
        for (int i = 0; i < 2; i++) {
            // When
            String move = app.inputFileLineReader.readLine()
            logger.info("{}: {}", i, move)

            Duration turnTime = Duration.between(last, LocalDateTime.now())
            last = LocalDateTime.now()

            // Then
            long turnTimeMillis = turnTime.toMillis()
            logger.info("Turn time (millis): {}", turnTimeMillis)
            assertTrue(turnTimeMillis < 50)
        }

        // Finally
        app.stop()
    }

}
