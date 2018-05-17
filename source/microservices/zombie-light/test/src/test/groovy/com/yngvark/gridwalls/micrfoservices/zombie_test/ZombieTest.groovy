package com.yngvark.gridwalls.micrfoservices.zombie_test

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.yngvark.communicate_through_named_pipes.RetrySleeper
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
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern

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
    void should_move_within_map_borders_after_receiving_map_info() throws Exception {
        // Given
        app = NamedPipeProcessStarter.start("--nosleep", "-seed=123")
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

            assertTrue(timeSinceLastMove.toMillis() < 1100,
                    "Time used for a move was: " + timeSinceLastMove)

            String[] parts = StringUtils.split(move, " ", 3)
            assertEquals("/publishTo Zombie", parts[0] + " " + parts[1])

            Move m = gson.fromJson(parts[2], Move.class)
            gatherMinMax(gatherer, m)

            // Then
            assertTrue(m.getX() <= 14 && m.getX() >= 0
                    && m.getY() <= 9 && m.getY() >= 0,
                    "x: " + m.getX() + ", y: " + m.getY())
        }

        // Then
        assertEquals(0, gatherer.min("x"))
        assertEquals(14, gatherer.max("x"))

        assertEquals(0, gatherer.min("y"))
        assertEquals(9, gatherer.max("y"))
    }

    private void gatherMinMax(MaxMinGatherer gatherer, Move m) {
        gatherer.add("x", m.getX())
        gatherer.add("y", m.getY())
    }

    @Test
    void moves_should_be_deterministic_given_seed() throws Exception {
        // Given
        app = NamedPipeProcessStarter.start("--nosleep", "-seed=123")
        def gson = new Gson()

        AppLineReader.readLine(app) // subscription
        AppLineReader.readLine(app) // request for map info
        String mapInfo = gson.toJson(new MapInfo(15, 10))
        app.outputFileWriter.write("[Zombie_MapInfo] " + mapInfo)

        // This file is generated by copying and pasting the output of this test to the file.
        def expectedMovesReader = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream("/expectedMoves.txt")))

        for (int i = 0; i < 1000; i++) {
            // When
            String moveTxt = app.inputFileLineReader.readLine()
            logger.debug(moveTxt)

            String[] parts = StringUtils.split(moveTxt, " ", 3)
            Move move = gson.fromJson(parts[2], Move.class)

            // Then
            Move expectedMove = getNextExpectedMove(gson, expectedMovesReader)
            assertEquals(expectedMove, move)
        }
    }

    private Move getNextExpectedMove(Gson gson, BufferedReader expectedMovesReader) throws IOException {
        String moveTxt

        while (true) {
            String line = expectedMovesReader.readLine()
            Pattern p = Pattern.compile("/publishTo Zombie (.*)")
            Matcher m = p.matcher(line)
            if (!m.find())
                continue

            moveTxt = m.group(1)
            break
        }

        try {
            return gson.fromJson(moveTxt, Move.class)
        } catch (JsonSyntaxException e) {
            throw new RuntimeException("Could not parse: " + moveTxt, e)
        }
    }

    @Test
    void should_move_at_most_1_distance_per_move() throws Exception {
        // Given
        app = NamedPipeProcessStarter.start("--nosleep", "-seed=123")
        def gson = new Gson()

        AppLineReader.readLine(app) // subscription
        AppLineReader.readLine(app) // request for map info
        String mapInfo = gson.toJson(new MapInfo(15, 10))
        app.outputFileWriter.write("[Zombie_MapInfo] " + mapInfo)

        Move lastMove
        for (int i = 0; i < 1000; i++) {
            // When
            String moveTxt = app.inputFileLineReader.readLine()
            logger.debug(moveTxt)

            String[] parts = StringUtils.split(moveTxt, " ", 3)
            Move move = gson.fromJson(parts[2], Move.class)
            if (i == 0) {
                lastMove = move
                continue
            }

            // Then
            int distanceX = Math.abs(lastMove.getX() - move.getX())
            int distanceY = Math.abs(lastMove.getY() - move.getY())
            assertTrue(distanceX <= 1, "DistanceX: " + distanceX + ". lastMove: " + lastMove + ", move: " + move)
            assertTrue(distanceY <= 1, "DistanceY: " + distanceY + ". lastMove: " + lastMove + ", move: " + move)

            lastMove = move
        }
    }
}
