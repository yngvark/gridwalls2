package com.yngvark.gridwalls.microservices.zombie.game;

import com.yngvark.gridwalls.core.Coordinate;
import com.yngvark.gridwalls.core.CoordinateSerializer;

import java.util.UUID;

public class ZombieMovedSerializer {
    private final CoordinateSerializer coordinateSerializer;
    private final String prefix = "[" + ZombieMoved.class.getSimpleName() + "] ";

    public ZombieMovedSerializer(CoordinateSerializer coordinateSerializer) {
        this.coordinateSerializer = coordinateSerializer;
    }

    public String serialize(ZombieMoved zombieMoved) {
        String targetCoordinate = coordinateSerializer.serialize(zombieMoved.getTargetCoordinate());
        return prefix + "id=" + zombieMoved.getId() + " tc=" + targetCoordinate;
    }

    public ZombieMoved deserialize(String serialized) {
        int idStart = serialized.indexOf("id=");
        int tcStart = serialized.indexOf("tc=");

        String idTxt = serialized.substring(idStart + 3, tcStart - 1);
        String coordsTxt = serialized.substring(tcStart + 3);

        UUID id = UUID.fromString(idTxt);
        Coordinate targetCoordinate = coordinateSerializer.deserialize(coordsTxt);
        return new ZombieMoved(id, targetCoordinate);
    }
}
