package com.yngvark.gridwalls.microservices.zombie.game;

import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;
import com.yngvark.gridwalls.microservices.zombie.game.serialize_events.JsonSerializer;

import java.util.Random;

public class GameFactory {
    public static Game create(OutputFileWriter outputFileWriter) {
        return new Game(
                outputFileWriter,
                new GameLogic(
                        new ThreadSleeper(),
                        new JsonSerializer(),
                        new Random()
                        )
        );
    }
}
