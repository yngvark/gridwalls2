package com.yngvark.gridwalls.netcom.gameconfig;

import com.yngvark.gridwalls.core.MapDimensions;

public class GameConfigDeserializer {
    public GameConfig deserialize(String serialized) {
        int heightStart = serialized.indexOf("mapHeight=");
        int widthStart = serialized.indexOf("mapWidth=");

        String heightTxt = serialized.substring(heightStart + "mapHeight=".length(), widthStart - 1);
        String widthTxt = serialized.substring(widthStart + "mapWidth=".length());

        int mapHeight = Integer.parseInt(heightTxt);
        int mapWidth = Integer.parseInt(widthTxt);

        return GameConfig.builder()
                .mapDimensions(new MapDimensions(mapWidth, mapHeight))
                .build();
    }
}
