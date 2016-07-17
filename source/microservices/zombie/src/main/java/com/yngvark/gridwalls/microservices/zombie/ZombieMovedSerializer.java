package com.yngvark.gridwalls.microservices.zombie;

import java.util.regex.Pattern;

class ZombieMovedSerializer {
    private final CoordinateSerializer coordinateSerializer;
    String prefix = "[" + ZombieMoved.class.getSimpleName() + "] ";

    public ZombieMovedSerializer(CoordinateSerializer coordinateSerializer) {
        this.coordinateSerializer = coordinateSerializer;
    }

    public String serialize(ZombieMoved zombieMoved) {
        String targetCoordinate = CoordinateSerializer.serialize(zombieMoved.getTargetCoordinate());
        return prefix + targetCoordinate;
    }

    public ZombieMoved deserialize(String serialized) {
        String coordsTxt = serialized.substring(prefix.length());

        Coordinate targetCoordinate = CoordinateSerializer.deserialize(coordsTxt);
        return new ZombieMoved(targetCoordinate);
    }
}
