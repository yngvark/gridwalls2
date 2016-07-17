package com.yngvark.gridwalls.microservices.zombie;

import java.util.HashSet;
import java.util.Set;

class MapCoordinates {
    private final int width;
    private final int height;

    public MapCoordinates(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Coordinate middle() {
        return new Coordinate(
                Math.floorDiv(width, 2),
                Math.floorDiv(height, 2)
        );
    }

    public int getSouthEdge() {
        return 0;
    }

    public int getWestEdge() {
        return 0;
    }

    public int getEastEdge() {
        return width - 1;
    }

    public int getNorthEdge() {
        return height - 1;
    }

    public Set<Coordinate> allCoordinates() {
        Set<Coordinate> coords = new HashSet<>();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                coords.add(new Coordinate(x, y));
            }
        }

        return coords;
    }
}
