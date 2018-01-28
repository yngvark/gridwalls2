package com.yngvark.gridwalls.microservices.zombie.run_game.serialize_msgs;

import com.yngvark.gridwalls.microservices.zombie.run_app.JsonSerializer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JsonSerializerTest {
    @Test
    public void error_should_contain_text_to_deserialize_if_it_fails() {
        // Given
        JsonSerializer jsonSerializer = new JsonSerializer();

        // When
        Throwable exception = assertThrows(RuntimeException.class, () -> {
            jsonSerializer.deserialize("something that will fail", Object.class);
        });

        // Then
        assertTrue(exception.getMessage().contains("something that will fail"));
    }

}