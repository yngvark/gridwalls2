package com.yngvark.gridwalls.microservices.zombie.game;

import com.yngvark.communicate_through_named_pipes.output.OutputFileWriter;

public class GameFactory {
    public static Game create(OutputFileWriter outputFileWriter) {
        return new Game(
                outputFileWriter,
                new GameLogic(
                        new ThreadSleeper(),
                        new JsonSerializer()
                        )
        );
    }
}
