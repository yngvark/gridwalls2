package com.yngvark.gridwalls.microservices.zombie.game;

import java.util.Random;
import java.util.UUID;

import com.yngvark.gridwalls.core.Coordinate;
import com.yngvark.gridwalls.core.CoordinateFactory;
import com.yngvark.gridwalls.core.Direction;

class Zombie {
    private final CoordinateFactory coordinateFactory;
    private final UUID id;

    private Coordinate coordinate;

    public Zombie(
            CoordinateFactory coordinateFactory,
            UUID id,
            Coordinate coordinate) {
        this.coordinateFactory = coordinateFactory;

        this.id = id;
        this.coordinate = coordinate;
    }

    public ZombieMoved nextTurn() {
        return move();
    }

    private ZombieMoved move() {
        coordinate = decideNewCoordinate();
        return new ZombieMoved(id, coordinate);
    }

    private Coordinate decideNewCoordinate() {
        Coordinate newCoordinate;
        do {
            Direction direction = randomDirection();
            newCoordinate = coordinateFactory.move(coordinate, direction);
        } while (newCoordinate.equals(coordinate));

        return newCoordinate;
    }

    private Direction randomDirection() {
        Direction[] directions = Direction.values();
        return directions[new Random().nextInt(directions.length)];
    }

}
