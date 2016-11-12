package com.yngvark.gridwalls.netcom.gameconfig;

import com.yngvark.gridwalls.core.MapDimensions;
import org.apache.commons.lang3.StringUtils;

public class Deserializer {
    public GameConfig deserialize(String serialized) {
        try {
            String[] splitted = StringUtils.split(serialized, " ");

            int mapHeight = Integer.parseInt(getValue(splitted[1]));
            int mapWidth = Integer.parseInt(getValue(splitted[2]));
            int sleepTimeMillisBetweenTurns = Integer.parseInt(getValue(splitted[3]));

            return GameConfig.builder()
                    .mapDimensions(new MapDimensions(mapWidth, mapHeight))
                    .sleepTimeMillisBetweenTurns(sleepTimeMillisBetweenTurns)
                    .build();
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeException("Cannot deserialize: " + serialized, e);
        }
    }

    private String getValue(String keyValuePair) {
        try {
            return StringUtils.split(keyValuePair, "=")[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new RuntimeException("Error when getting value from text: " + keyValuePair, e);
        }
    }
}
