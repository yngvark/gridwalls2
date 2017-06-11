package com.yngvark.gridwalls.microservices.zombie.run_game.get_map_info;

public class MapInfo {
    public final int width;
    public final int height;

    public MapInfo(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return "MapInfo{" +
                "width=" + width +
                ", height=" + height +
                '}';
    }
}
