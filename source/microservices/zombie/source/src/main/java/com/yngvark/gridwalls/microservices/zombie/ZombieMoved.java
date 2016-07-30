package com.yngvark.gridwalls.microservices.zombie;

import java.util.UUID;

class ZombieMoved {
    private final UUID id;
    private final Coordinate targetCoordinate;

    public ZombieMoved(UUID id, Coordinate targetCoordinate) {
        this.id = id;
        this.targetCoordinate = targetCoordinate;
    }

    public UUID getId() {
        return id;
    }

    public Coordinate getTargetCoordinate() {
        return targetCoordinate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ZombieMoved that = (ZombieMoved) o;

        return targetCoordinate.equals(that.targetCoordinate);

    }

    @Override
    public int hashCode() {
        return targetCoordinate.hashCode();
    }
}
