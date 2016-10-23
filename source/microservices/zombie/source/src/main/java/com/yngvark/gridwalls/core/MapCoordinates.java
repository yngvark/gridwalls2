package com.yngvark.gridwalls.core;

import java.util.HashSet;
import java.util.Set;

public class MapCoordinates {
    private final MapDimensions mapDimensions;

    public MapCoordinates(MapDimensions mapDimensions) {
        this.mapDimensions = mapDimensions;
    }

    public Coordinate center() {
        return new Coordinate(
                Math.floorDiv(mapDimensions.getWidth(), 2),
                Math.floorDiv(mapDimensions.getHeight(), 2)
        );
    }

    public int getSouthEdge() {
        return 0;
    }

    public int getWestEdge() {
        return 0;
    }

    public int getEastEdge() {
        return mapDimensions.getWidth() - 1;
    }

    public int getNorthEdge() {
        return mapDimensions.getHeight() - 1;
    }

    public Set<Coordinate> allCoordinates() {
        Set<Coordinate> coords = new HashSet<>();

        for (int x = 0; x < mapDimensions.getWidth(); x++) {
            for (int y = 0; y < mapDimensions.getHeight(); y++) {
                coords.add(new Coordinate(x, y));
            }
        }

        return coords;
    }
}
