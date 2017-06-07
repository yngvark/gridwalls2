package com.yngvark.gridwalls.microservices.zombie.game;

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
