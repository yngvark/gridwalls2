package com.yngvark.gridwalls.netcom.gameconfig;

import com.yngvark.gridwalls.core.MapDimensions;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeserializerTest {
    @Test
    public void should_deserialize_expected_server_response() throws Exception {
        // Given
        Deserializer deserializer = new Deserializer();
        String serialized = "[GameInfo] mapWidth=10 mapWidth=10 sleepTimeMillisBetweenTurns=1200";

        // When
        GameConfig gameConfig = deserializer.deserialize(serialized);

        // Then
        assertEquals(
                GameConfig.builder()
                        .mapDimensions(new MapDimensions(10, 10))
                        .sleepTimeMillisBetweenTurns(1200)
                .build(),
                gameConfig);
    }

    @Test
    public void error_if_no_map_height() throws Exception {
        // Given
        Deserializer deserializer = new Deserializer();
        String serialized = "[GameInfo] mapWidth=10";

        // When
        Throwable exception = assertThrows(RuntimeException.class,  () -> deserializer.deserialize(serialized));

        // Then
        System.out.println(exception);
        assertTrue(exception.getMessage().contains("Cannot deserialize: " + serialized));
    }

    @Test
    public void error_if_no_map_width() throws Exception {
        // Given
        Deserializer deserializer = new Deserializer();
        String serialized = "[GameInfo] mapHeight=10";

        // When
        Throwable exception = assertThrows(RuntimeException.class,  () -> deserializer.deserialize(serialized));

        // Then
        System.out.println(exception);
        assertTrue(exception.getMessage().contains("Cannot deserialize: " + serialized));
    }

    @Test
    public void error_if_no_sleep_time() throws Exception {
        // Given
        Deserializer deserializer = new Deserializer();
        String serialized = "[GameInfo] mapHeight=10 mapWidth=10";

        // When
        Throwable exception = assertThrows(RuntimeException.class,  () -> deserializer.deserialize(serialized));

        // Then
        System.out.println(exception);
        assertTrue(exception.getMessage().contains("Cannot deserialize: " + serialized));
    }
}