package com.yngvark.gridwalls.micrfoservices.zombie_test;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.yngvark.named_piped_app_runner.NamedPipeProcess;
import com.yngvark.named_piped_app_runner.NamedPipeProcessStarter;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.slf4j.LoggerFactory.getLogger;

public class ZombieTest {
    public final Logger logger = getLogger(ZombieTest.class);
    private NamedPipeProcess app;

    @AfterEach
    public void afterEach() throws Exception {
        logger.info("--- afterEach");
        app.stop();
    }

    @Test
    public void should_move_within_map_after_receiving_map_info() throws Exception {
        // Given
        app = NamedPipeProcessStarter.start("--nosleep -seed=123");
        Gson gson = new Gson();
        MaxMinGatherer gatherer = new MaxMinGatherer();

        // When
        String subscription = AppLineReader.readLine(app);

        // Then
        assertEquals("/subscribeTo Zombie_MapInfo", subscription);

        String mapInfoRequest = gson.toJson(
                new MapInfoRequest().replyToTopic("Zombie_MapInfo"));
        String publish = AppLineReader.readLine(app);
        assertEquals("/publishTo MapInfoRequests " + mapInfoRequest, publish);

        // And given
        String mapInfo = gson.toJson(
                new MapInfo(15, 10));
        app.outputFileWriter.write("[Zombie_MapInfo] " + mapInfo);
        LocalDateTime moveTime = LocalDateTime.now();

        // When
        for (int i = 0; i < 1000; i++) {
            String move = app.inputFileLineReader.readLine();
            logger.info("{}: {}", i, move);

            LocalDateTime now = LocalDateTime.now();
            Duration timeSinceLastMove = Duration.between(moveTime, now);
            moveTime = now;

            assertTrue(timeSinceLastMove.toMillis() < 1000,
                    "Time used for a move was: " + timeSinceLastMove);

            String[] parts = StringUtils.split(move, " ", 3);
            assertEquals("/publishTo Zombie", parts[0] + " " + parts[1]);

            Move m = gson.fromJson(parts[2], Move.class);
            gatherMinMax(gatherer, m);

            // Then
            assertTrue(m.toX <= 10 && m.toX >= 1
                    && m.toY <= 15 && m.toY >= 1);
        }

        // Then
        assertEquals(10, gatherer.max("x"));
        assertEquals(15, gatherer.max("y"));
    }

    private void gatherMinMax(MaxMinGatherer gatherer, Move m) {
        gatherer.add("x", m.toX);
        gatherer.add("y", m.toY);
    }

    @Test
    public void moves_should_be_deterministic_given_seed() throws Exception {
        // Given
        app = NamedPipeProcessStarter.start("--nosleep -seed=123");
        Gson gson = new Gson();
        MaxMinGatherer gatherer = new MaxMinGatherer();

        AppLineReader.readLine(app); // subscription
        AppLineReader.readLine(app); // request for map info

        String mapInfo = gson.toJson(new MapInfo(15, 10));
        app.outputFileWriter.write("[Zombie_MapInfo] " + mapInfo);

        BufferedReader expectedMovesReader = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream("/expectedMoves.txt")));

        // When
        for (int i = 0; i < 1000; i++) {
            // Then
            String move = app.inputFileLineReader.readLine();
            String[] parts = StringUtils.split(move, " ", 3);
            Move m = gson.fromJson(parts[2], Move.class);

            Move expectedMove = getNextExpectedMove(gson, expectedMovesReader);
            assertEquals(expectedMove, m);
        }

        // Then
        assertEquals(10, gatherer.max("x"));
        assertEquals(15, gatherer.max("y"));
    }


    private Move getNextExpectedMove(Gson gson, BufferedReader expectedMovesReader) throws IOException {
        String line = expectedMovesReader.readLine();
        String moveTxt = parseMove(line);

        try {
            return gson.fromJson(moveTxt, Move.class);
        } catch (JsonSyntaxException e) {
            logger.error("Could not parse: {}", moveTxt);
            throw e;
        }
    }

    private String parseMove(String line) {
        Pattern p = Pattern.compile("/publishTo Zombie (.*)\"");
        Matcher m = p.matcher(line);
        m.find();
        return m.group(1);
    }

}

