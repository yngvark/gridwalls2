package com.yngvark.gridwalls.microservices.zombie.receive_map_info;

import com.yngvark.gridwalls.microservices.zombie.common.MapInfo;
import com.yngvark.gridwalls.microservices.zombie.common.MapInfoRequest;
import com.yngvark.gridwalls.microservices.zombie.common.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class MapInfoReceiver {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final BufferedReader bufferedReader;
    private final BufferedWriter bufferedWriter;
    private final Serializer serializer;

    public MapInfoReceiver(BufferedReader bufferedReader, BufferedWriter bufferedWriter,
            Serializer serializer) {
        this.bufferedReader = bufferedReader;
        this.bufferedWriter = bufferedWriter;
        this.serializer = serializer;
    }

    public MapInfo getMapInfo() {
        write("/subscribeTo Zombie_MapInfo");

        String mapInfoRequest = serializer.serialize(
                new MapInfoRequest().replyToTopic("Zombie_MapInfo"));
        write("/publishTo MapInfoRequests " + mapInfoRequest);

        logger.info("Waiting for reply with map info.");
        String reply = read();

        logger.info("Reply: {}", reply);
        String mapInfoTxt = reply.split(" ", 2)[1];
        MapInfo mapInfo = serializer.deserialize(mapInfoTxt, MapInfo.class);

        return mapInfo;
    }

    private String read() {
        try {
            return bufferedReader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void write(String string) {
        try {
            logger.debug("Sending: {}", string);
            bufferedWriter.write(string);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
