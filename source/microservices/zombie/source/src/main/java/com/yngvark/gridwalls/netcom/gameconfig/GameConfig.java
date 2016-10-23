package com.yngvark.gridwalls.netcom.gameconfig;

import com.yngvark.gridwalls.core.MapDimensions;

public class GameConfig {
    private final MapDimensions mapDimensions;

    public static Builder builder() {
        return new Builder();
    }

    public GameConfig(MapDimensions mapDimensions) {
        this.mapDimensions = mapDimensions;
    }

    public MapDimensions getMapDimensions() {
        return mapDimensions;
    }

    public static class Builder {
        private MapDimensions mapDimensions;

        public GameConfig build() {
            return new GameConfig(mapDimensions);
        }

        public Builder mapDimensions(MapDimensions mapDimensions) {
            this.mapDimensions = mapDimensions;
            return this;
        }
    }
}
