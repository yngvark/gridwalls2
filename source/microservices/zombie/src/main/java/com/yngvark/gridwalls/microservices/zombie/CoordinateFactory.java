package com.yngvark.gridwalls.microservices.zombie;

import java.util.HashMap;
import java.util.Map;

class CoordinateFactory {
    private final MapCoordinates mapCoordinates;
    private final Map<Direction, Coordinate> coordinateChanges;

    public CoordinateFactory(MapCoordinates mapCoordinates) {
        this.mapCoordinates = mapCoordinates;

        this.coordinateChanges = new HashMap<>();
        coordinateChanges.put(Direction.NORTH, new Coordinate(0, -1));
        coordinateChanges.put(Direction.EAST, new Coordinate(1, 0));
        coordinateChanges.put(Direction.WEST, new Coordinate(-1, 0));
        coordinateChanges.put(Direction.SOUTH, new Coordinate(0, 1));
    }

    public Coordinate move(Coordinate from, Direction direction) {
        Coordinate addend = coordinateChanges.get(direction);

        int toX = moveX(from, addend);
        int toY = moveY(from, addend);

        return new Coordinate(toX, toY);
    }

    private int moveY(Coordinate from, Coordinate addend) {
        int toY = from.getY() + addend.getY();
        toY = Math.min(toY, mapCoordinates.getNorthEdge());
        toY = Math.max(toY, mapCoordinates.getSouthEdge());
        return toY;
    }

    private int moveX(Coordinate from, Coordinate addend) {
        int toX = from.getX() + addend.getX();
        toX = Math.min(toX, mapCoordinates.getEastEdge());
        toX = Math.max(toX, mapCoordinates.getWestEdge());
        return toX;
    }

}
