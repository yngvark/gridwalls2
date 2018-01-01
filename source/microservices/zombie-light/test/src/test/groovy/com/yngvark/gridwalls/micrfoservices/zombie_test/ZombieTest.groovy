package com.yngvark.gridwalls.micrfoservices.zombie_test

import com.google.gson.Gson
import com.yngvark.gridwalls.microservices.zombie.common.MapInfoRequest
import com.yngvark.gridwalls.microservices.zombie.common.MapInfo
import com.yngvark.gridwalls.microservices.zombie.move_zombie.Move
import com.yngvark.named_piped_app_runner.NamedPipeProcess
import com.yngvark.named_piped_app_runner.NamedPipeProcessStarter
import org.apache.commons.lang3.StringUtils
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.time.Duration
import java.time.LocalDateTime

import static org.junit.jupiter.api.Assertions.*

class ZombieTest {
    public final Logger logger = LoggerFactory.getLogger(ZombieTest.class)
    private NamedPipeProcess app

    @AfterEach
    void afterEach() throws Exception {
        logger.info("--- afterEach")
        app.stop()
    }

    @Test
    void should_move_within_map_after_receiving_map_info() throws Exception {
        // Given
        app = NamedPipeProcessStarter.start("--nosleep -seed=123")
        def gson = new Gson()
        def gatherer = new MaxMinGatherer()

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
        LocalDateTime moveTime = LocalDateTime.now()

        // When
        for (int i = 0; i < 1000; i++) {
            String move = app.inputFileLineReader.readLine()
            logger.info("{}: {}", i, move)

            LocalDateTime now = LocalDateTime.now()
            Duration timeSinceLastMove = Duration.between(moveTime, now)
            moveTime = now

            assertTrue(timeSinceLastMove.toMillis() < 1000,
                    "Time used for a move was: " + timeSinceLastMove)

            String[] parts = StringUtils.split(move, " ", 3)
            assertEquals("/publishTo Zombie", parts[0] + " " + parts[1])

            Move m = gson.fromJson(parts[2], Move.class)
            gatherMinMax(gatherer, m)

            // Then
            assertTrue(m.toX <= 10 && m.toX >= 1
                    && m.toY <= 15 && m.toY >= 1)
        }

        // Then
        assertEquals(10, gatherer.max("x"))
        assertEquals(15, gatherer.max("y"))
    }

    private void gatherMinMax(MaxMinGatherer gatherer, Move m) {
        gatherer.add("x", m.getX())
        gatherer.add("y", m.getY())
    }

}
