package com.yngvark.gridwalls.micrfoservices.zombie_test;

import com.google.gson.Gson;
import com.yngvark.named_piped_app_runner.NamedPipeProcess;
import com.yngvark.named_piped_app_runner.NamedPipeProcessStarter;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.slf4j.LoggerFactory.getLogger;

public class ProcessTest {
    public final Logger logger = getLogger(ProcessTest.class);

    @Test
    public void should_subscribe_to_requeusts_topic() throws Exception {
        // Given
        NamedPipeProcess app = NamedPipeProcessStarter.start();

        // When
        String msg = readLine(app);

        // Then
        assertEquals("/publishTo MapInfoRequests", msg);

        // Finally
        app.stop();
    }

    @Test
    public void should_move_within_map_after_receiving_map_info() throws Exception {
        // Given
        NamedPipeProcess app = NamedPipeProcessStarter.start();
        Gson gson = new Gson();

        // When
        String subscription = readLine(app);

        // Then
        assertEquals("/subscribeTo Zombie_MapInfo", subscription);

        String mapInfoRequest = gson.toJson(
                new MapInfoRequest().replyToTopic("Zombie_MapInfo"));
        String publish = readLine(app);
        assertEquals("/publishTo MapInfoRequests " + mapInfoRequest, publish);

        // And given
        String mapInfo = gson.toJson(
                new MapInfo(15, 10));
        app.outputFileWriter.write("[Zombie_MapInfo] " + mapInfo);

        // When
        for (int i = 0; i < 3; i++) {
            // Then
            String move = app.inputFileLineReader.readLine();
            String[] parts = StringUtils.split(move, " ", 3);
            assertEquals("/publishTo Zombie", parts[0] + " " + parts[1]);

            Move m = gson.fromJson(parts[2], Move.class);
            assertTrue(m.toX <= 15 && m.toX >= 1
                    && m.toY <= 10 && m.toY >= 1);
        }

        // Finally
        app.stop();
    }

    private String readLine(NamedPipeProcess app) {
        return assertTimeoutPreemptively(Duration.ofMillis(200), app.inputFileLineReader::readLine);
    }

}

