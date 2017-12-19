package com.yngvark.gridwalls.microservices.zombie.move_zombie;

public class MapInfo {
    private final int width;
    private final int height;

    public MapInfo(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        MapInfo mapInfo = (MapInfo) o;

        if (width != mapInfo.width)
            return false;
        return height == mapInfo.height;
    }

    @Override
    public int hashCode() {
        int result = width;
        result = 31 * result + height;
        return result;
    }
}
