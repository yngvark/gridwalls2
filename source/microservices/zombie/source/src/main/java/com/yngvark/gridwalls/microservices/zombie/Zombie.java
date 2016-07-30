package com.yngvark.gridwalls.microservices.zombie;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import com.rabbitmq.client.Channel;

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

    public ZombieMoved nextTurn() throws IOException {
        return move();
    }

    private ZombieMoved move() throws IOException {
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
