package com.yngvark.gridwalls.microservices.zombie;

import com.yngvark.gridwalls.netcom.NetCom;

import java.util.Random;

class Zombie {
    private final NetCom netCom;
    private final CoordinateFactory coordinateFactory;
    private final ZombieMovedSerializer zombieMovedSerializer;

    private Coordinate coordinate;

    public Zombie(
            NetCom netCom,
            CoordinateFactory coordinateFactory,
            ZombieMovedSerializer zombieMovedSerializer,
            Coordinate coordinate) {
        this.netCom = netCom;
        this.zombieMovedSerializer = zombieMovedSerializer;

        this.coordinate = coordinate;
        this.coordinateFactory = coordinateFactory;
    }

    public void nextTurn() {
        move();
    }

    private void move() {
        coordinate = decideNewCoordinate();
        ZombieMoved zombieMoved = new ZombieMoved(coordinate);
        netCom.publish(zombieMovedSerializer.serialize(zombieMoved));
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
