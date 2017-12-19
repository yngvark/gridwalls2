package zombie.gameloop;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import zombie.Main;
import zombie.move_zombie.MapInfo;
import zombie.move_zombie.Move;
import zombie.move_zombie.Zombie;
import zombie.move_zombie.ZombieFactory;
import zombie.produce_events.EventProducer;
import zombie.react_to_events.EventHandler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

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

        Zombie zombie = ZombieFactory.create();
        Gson gson = new Gson();

        GameLoopRunner gameLoopRunner = new GameLoopRunner(
                bufferedFromTestReader,
                EventHandler.create(zombie),
                EventProducer.create(
                        zombie,
                        bufferedToTestWriter),
                sleeper,
                new Random(2342));

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

        Zombie zombie = ZombieFactory.create();
        Gson gson = new Gson();

        GameLoopRunner gameLoopRunner = new GameLoopRunner(
                bufferedFromTestReader,
                EventHandler.create(zombie),
                EventProducer.create(
                        zombie,
                        bufferedToTestWriter),
                sleeper,
                new Random(54645));

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

    @Test
    @Disabled
    public void zombie_should_move_expected_trail_given_specfic_seed() throws IOException {
        // Given
        Sleeper sleeper = (timeUnit, count) -> {};

        Zombie zombie = ZombieFactory.create();
        Gson gson = new Gson();
        Random random = new Random(345983);

        GameLoopRunner gameLoopRunner = new GameLoopRunner(
                bufferedFromTestReader,
                EventHandler.create(zombie),
                EventProducer.create(
                        zombie,
                        bufferedToTestWriter),
                sleeper,
                random
        );

        MapInfo mapInfo = new MapInfo(15, 10);
        bufferedToGameWriter.write(gson.toJson(mapInfo));
        BufferedReader expectedMovesReader = new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream("/expectedMoves.txt")));
        assertTrue(expectedMovesReader.ready());

        for (int i = 0; i < 10000; i++) {
            // When
            gameLoopRunner.runOneIteration();

            // Then
            assertTrue(bufferedFromGameReader.ready());

            String zombieMoveSerialized = bufferedFromGameReader.readLine();
            Move zombieMove = gson.fromJson(zombieMoveSerialized, Move.class);

            String[] xAndY = expectedMovesReader.readLine().split(",");
            Move expectedMove = new Move(Integer.parseInt(xAndY[0]), Integer.parseInt(xAndY[1]));
            assertEquals(expectedMove, zombieMove);
        }
    }
}
