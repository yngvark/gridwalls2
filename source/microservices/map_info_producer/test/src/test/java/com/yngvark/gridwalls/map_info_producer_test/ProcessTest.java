package com.yngvark.gridwalls.map_info_producer_test;

import com.google.gson.Gson;
import com.yngvark.named_piped_app_runner.AppLineReader;
import com.yngvark.named_piped_app_runner.NamedPipeProcess;
import com.yngvark.named_piped_app_runner.NamedPipeProcessStarter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProcessTest {
    @Test
    public void should_subscribe_to_requeusts_topic() throws Exception {
        // Given
        NamedPipeProcess app = NamedPipeProcessStarter.start();

        // When
        String subscription = AppLineReader.readLine(app);

        // Then
        assertEquals("/subscribeTo MapInfoRequests", subscription);
    }

    @Test
    public void should_reply_map_info_to_reply_topic() throws Exception {
        // Given
        NamedPipeProcess app = NamedPipeProcessStarter.start();

        String subscription = AppLineReader.readLine(app);
        assertEquals("/subscribeTo MapInfoRequests", subscription);

        MapInfoRequest request = new MapInfoRequest().replyToTopic("TestReplyTopic");
        String requestSerialized = new Gson().toJson(request);

        // When
        app.outputFileWriter.write("[MapInfoRequests] " + requestSerialized);

        // Then
        MapInfo expected = new MapInfo(15, 10);
        String expectedMapInfo = new Gson().toJson(expected);

        String publish = AppLineReader.readLine(app);

        assertEquals("/publishTo TestReplyTopic " + expectedMapInfo, publish);
    }


}

