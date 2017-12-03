package zombie.gameloop;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
    public void az2() {
        System.out.println(new File("q").getAbsolutePath());
        System.out.println(new File("a").getAbsolutePath());
        System.out.println(getClass().getClassLoader().getResource("a"));
    }

    @Test
    public void az() {
        List<String> resources = new ArrayList<>();
        resources.add("a");
        resources.add("\\a");
        resources.add("/a");
        resources.add("//a");
        resources.add("resources/a");
        resources.add("/resources/a");

        for (String resource : resources) {
            System.out.println("------------------" + resource);
            java.net.URL a1 = getClass().getResource(resource);
            System.out.println(a1);
            java.net.URL a4 = Thread.currentThread().getContextClassLoader().getResource(resource);
            System.out.println(a4);
            java.net.URL a2 = GameLoopTest.class.getResource(resource);
            System.out.println(a2);
            java.net.URL a3 = Main.class.getResource(resource);
            System.out.println(a3);
            System.out.println(new File(resource).exists());
            System.out.println(getClass().getClassLoader().getResource(resource));
        }

        System.out.println(getClass().getResourceAsStream("/expectedmoves.txt"));

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
    public void zombie_should_move_expected_trail_given_specfic_seed() throws IOException {
        // Given
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("expectedmoves.txt");

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
                new InputStreamReader(getClass().getResourceAsStream("expectedmoves.txt")));
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

    @Test
    public void zombie_should_move_randomly_around() throws IOException {
        // Given
        PipedWriter pipedWriter = new PipedWriter();
        BufferedReader bufferedReader = new BufferedReader(new PipedReader(pipedWriter));
        BufferedWriter bufferedWriter = new BufferedWriter(pipedWriter);

        Sleeper sleeper = (timeUnit, count) -> {};

        Zombie zombie = ZombieFactory.create();
        Gson gson = new Gson();

        GameLoopRunner gameLoopRunner = new GameLoopRunner(
                bufferedReader,
                EventHandler.create(zombie),
                EventProducer.create(
                        zombie,
                        bufferedWriter),
                sleeper,
                new Random(45645));

        MapInfo mapInfo = new MapInfo(15, 10);
        bufferedWriter.write(new Gson().toJson(mapInfo));

        // When
        for (int i = 0; i < 1000; i++) {
            gameLoopRunner.runOneIteration();
        }

        // Then
        Set<Integer> xs = new HashSet<>();
        Set<Integer> ys = new HashSet<>();

        for (int i = 0; i < 1000; i++) {
            assertTrue(bufferedReader.ready());

            String zombieMoveSerialized = bufferedReader.readLine();
            Move zombieMove = gson.fromJson(zombieMoveSerialized, Move.class);
//
//            assertTrue(zombieMove.getX() <= 15);
//            assertTrue(zombieMove.getX() >= 0);
//            assertTrue(zombieMove.getY() <= 10);
//            assertTrue(zombieMove.getY() >= 0);
//
//            xs.add(zombieMove.getX());
//            ys.add(zombieMove.getY());
        }



    }
}
