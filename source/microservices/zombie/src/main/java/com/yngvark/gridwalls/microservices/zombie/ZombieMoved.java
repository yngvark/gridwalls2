package com.yngvark.gridwalls.microservices.zombie;

class ZombieMoved {
    private final Coordinate targetCoordinate;

    public ZombieMoved(Coordinate targetCoordinate) {
        this.targetCoordinate = targetCoordinate;
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
