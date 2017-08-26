package com.yngvark.gridwalls.micrfoservices.zombie_test;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.yngvark.named_piped_app_runner.NamedPipeProcess;
import com.yngvark.named_piped_app_runner.NamedPipeProcessStarter;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.slf4j.LoggerFactory.getLogger;

public class MapInfoReceiverTest {
    public final Logger logger = getLogger(MapInfoReceiverTest.class);
    private NamedPipeProcess app;

    @AfterEach
    public void afterEach() throws Exception {
        logger.info("--- afterEach");
        app.stop();
    }

    @Test
    public void should_resend_map_info_request_within_2_seconds_if_no_reply() throws Exception {
        // Given
        app = NamedPipeProcessStarter.start();
        Gson gson = new Gson();

        String subscription = AppLineReader.readLine(app); // subscription
        logger.info("subscription: {}", subscription);
        String mapInfoRequestFirst = AppLineReader.readLine(app); // map info request
        logger.info("mapInfoRequestFirst: {}", mapInfoRequestFirst);

        for (int i = 0; i < 2; i++) {
            logger.info("Waiting for map info request... Attempt: {}", i);
            String mapInfoRequest = assertTimeoutPreemptively(
                    Duration.ofMillis(2000l),
                    () -> {
                        try {
                            return app.inputFileLineReader.readLine();
                        } catch (Throwable e) {
                            return "Farsken";
                        }
                    });
//
//            String mapInfoRequest2 = assertTimeoutPreemptively(
//                    Duration.ofMillis(5l),
//                    () -> "hei");

            logger.info("Result: " + mapInfoRequest);
        }

        logger.info("-------------- EXITING --------------");
    }


}

