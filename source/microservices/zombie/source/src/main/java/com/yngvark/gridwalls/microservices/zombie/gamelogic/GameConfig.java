package com.yngvark.gridwalls.microservices.zombie.gamelogic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder @NoArgsConstructor @AllArgsConstructor
@Getter
public class GameConfig {
    private int mapHeight;
    private int mapWidth;
}
