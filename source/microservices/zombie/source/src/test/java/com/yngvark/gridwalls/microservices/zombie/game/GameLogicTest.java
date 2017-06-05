package com.yngvark.gridwalls.microservices.zombie.game;

import com.yngvark.gridwalls.microservices.zombie.game.move.Move;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.slf4j.LoggerFactory.getLogger;

public class GameLogicTest {
    private final Logger logger = getLogger(getClass());

    @Test
    public void should_move_within_map() {
        // Given
        NoSleeper noSleeper = new NoSleeper();
        Serializer serializer = new JsonSerializer();
        GameLogic gameLogic = new GameLogic(
                noSleeper,
                serializer);

        MapInfo mapInfo = new MapInfo(10, 7);
        String mapInfoStr = serializer.serialize(mapInfo, MapInfo.class);
        gameLogic.messageReceived(mapInfoStr);

        // When
        for (int i = 0; i < 10; i++) {
            String msg = gameLogic.nextMsg();
            Move move = serializer.deserialize(msg, Move.class);
            logger.info(move.toString());
            assertTrue(isWithinMap(move, mapInfo));
        }
    }

    private boolean isWithinMap(Move move, MapInfo mapInfo) {
        return move.toX >= 1
                && move.toX <= mapInfo.width
                && move.toY >= 1
                && move.toY <= mapInfo.height;
    }

}