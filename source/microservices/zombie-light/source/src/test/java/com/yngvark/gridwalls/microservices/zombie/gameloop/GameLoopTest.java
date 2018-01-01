package com.yngvark.gridwalls.microservices.zombie.gameloop;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import com.yngvark.gridwalls.microservices.zombie.common.MapInfo;
import com.yngvark.gridwalls.microservices.zombie.move_zombie.Move;
import com.yngvark.gridwalls.microservices.zombie.move_zombie.Zombie;
import com.yngvark.gridwalls.microservices.zombie.move_zombie.ZombieFactory;
import com.yngvark.gridwalls.microservices.zombie.produce_events.EventProducer;
import com.yngvark.gridwalls.microservices.zombie.react_to_events.EventHandler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class GameLoopTest {

    private PipedWriter toGameWriter;
    private PipedReader fromGameReader;
    private PipedWriter toTestWriter;
    private PipedReader fromTestReader;
    private BufferedReader bufferedFromGameReader;
    private BufferedWriter bufferedToGameWriter;
    private BufferedReader bufferedFromTestReader;
    private BufferedWriter bufferedToTestWriter;

    @BeforeEach
    public void beforeEach() throws IOException {
        toGameWriter = new PipedWriter();
        fromGameReader = new PipedReader();

        toTestWriter = new PipedWriter();
        fromTestReader = new PipedReader();

        fromTestReader.connect(toGameWriter);
        fromGameReader.connect(toTestWriter);

        bufferedFromGameReader = new BufferedReader(fromGameReader);
        bufferedToGameWriter = new BufferedWriter(toGameWriter);

        bufferedFromTestReader = new BufferedReader(fromTestReader);
        bufferedToTestWriter = new BufferedWriter(toTestWriter);
    }

    @AfterEach
    public void afterEach() throws IOException {
        toGameWriter.close();
        fromGameReader.close();
        toTestWriter.close();
        fromTestReader.close();
    }

    @Test
    public void zombie_should_move() throws IOException {
        // Given
        Sleeper sleeper = (timeUnit, count) -> {};

        Zombie zombie = ZombieFactory.create(new MapInfo(15, 10), new Random(54645));
        Gson gson = new Gson();

        GameLoopRunner gameLoopRunner = new GameLoopRunner(
                bufferedFromTestReader,
                EventHandler.create(zombie),
                EventProducer.create(
                        zombie,
                        bufferedToTestWriter),
                sleeper);

        // When
        gameLoopRunner.runOneIteration();

        // Then
        assertTrue(bufferedFromGameReader.ready());

        String zombieMoveSerialized = bufferedFromGameReader.readLine();
        System.out.println(zombieMoveSerialized);
        gson.fromJson(zombieMoveSerialized, Move.class);
    }

    @Test
    public void zombie_should_move_within_map_borders() throws IOException {
        // Given
        Sleeper sleeper = (timeUnit, count) -> {};

        Zombie zombie = ZombieFactory.create(new MapInfo(15, 10), new Random(54645));
        Gson gson = new Gson();

        GameLoopRunner gameLoopRunner = new GameLoopRunner(
                bufferedFromTestReader,
                EventHandler.create(zombie),
                EventProducer.create(
                        zombie,
                        bufferedToTestWriter),
                sleeper
        );

        MapInfo mapInfo = new MapInfo(15, 10);
        bufferedToGameWriter.write(new Gson().toJson(mapInfo));

        for (int i = 0; i < 10000; i++) {
            // When
            gameLoopRunner.runOneIteration();

            // Then
            assertTrue(bufferedFromGameReader.ready());

            String zombieMoveSerialized = bufferedFromGameReader.readLine();
            Move zombieMove = gson.fromJson(zombieMoveSerialized, Move.class);

            assertTrue(zombieMove.getX() <= 15);
            assertTrue(zombieMove.getX() >= 0);
            assertTrue(zombieMove.getY() <= 10);
            assertTrue(zombieMove.getY() >= 0);
        }
    }

    @Test
    public void zombie_should_move_randomly() throws IOException {
//        TODO usikker på om disse testene burde vært unit eller integrasjonstester
    }

}
