package com.yngvark.gridwalls.micrfoservices.zombie_test;

import com.yngvark.named_piped_app_runner.NamedPipeProcess;
import com.yngvark.named_piped_app_runner.NamedPipeProcessStarter;
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
        String msg = assertTimeoutPreemptively(Duration.ofMillis(200), app.inputFileLineReader::readLine);

        // Then
        assertEquals("/subscribeTo MapInfoRequests", msg);

        // Finally
        app.stop();
    }

}

