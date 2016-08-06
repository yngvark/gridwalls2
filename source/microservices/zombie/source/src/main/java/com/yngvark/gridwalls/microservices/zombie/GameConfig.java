package com.yngvark.gridwalls.microservices.zombie;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder @NoArgsConstructor @AllArgsConstructor
@Getter
class GameConfig {
    private int mapHeight;
    private int mapWidth;
}
