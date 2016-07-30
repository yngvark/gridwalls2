package com.yngvark.gridwalls.microservices.zombie;

import java.util.UUID;
import java.util.regex.Pattern;

class ZombieMovedSerializer {
    private final CoordinateSerializer coordinateSerializer;
    String prefix = "[" + ZombieMoved.class.getSimpleName() + "] ";

    public ZombieMovedSerializer(CoordinateSerializer coordinateSerializer) {
        this.coordinateSerializer = coordinateSerializer;
    }

    public String serialize(ZombieMoved zombieMoved) {
        String targetCoordinate = CoordinateSerializer.serialize(zombieMoved.getTargetCoordinate());
        return prefix + "id=" + zombieMoved.getId() + " tc=" + targetCoordinate;
    }

    public ZombieMoved deserialize(String serialized) {
        // [ZombieMoved] id=abcd-efgh-ijkl tc=8,5

        int idStart = serialized.indexOf("id=");
        int tcStart = serialized.indexOf("tc=");

        String idTxt = serialized.substring(idStart + 3, tcStart - 1);
        String coordsTxt = serialized.substring(tcStart + 3);

        UUID id = UUID.fromString(idTxt);
        Coordinate targetCoordinate = CoordinateSerializer.deserialize(coordsTxt);
        return new ZombieMoved(id, targetCoordinate);
    }
}
