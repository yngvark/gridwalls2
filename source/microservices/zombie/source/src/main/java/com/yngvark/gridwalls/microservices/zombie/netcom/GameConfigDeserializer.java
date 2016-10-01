package com.yngvark.gridwalls.microservices.zombie.netcom;

import com.yngvark.gridwalls.microservices.zombie.gamelogic.GameConfig;

public class GameConfigDeserializer {
    public GameConfig deserialize(String serialized) {
        // "[GameInfo] mapHeight=10 mapWidth=10"
        int heightStart = serialized.indexOf("mapHeight=");
        int widthStart = serialized.indexOf("mapWidth=");

        String heightTxt = serialized.substring(heightStart + "mapHeight=".length(), widthStart - 1);
        String widthTxt = serialized.substring(widthStart + "mapWidth=".length());

        int mapHeight = Integer.parseInt(heightTxt);
        int mapWidth = Integer.parseInt(widthTxt);

        return GameConfig.builder()
                .mapHeight(mapHeight)
                .mapWidth(mapWidth)
                .build();
    }
}
