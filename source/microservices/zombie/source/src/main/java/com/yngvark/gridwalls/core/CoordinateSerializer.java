package com.yngvark.gridwalls.core;

public class CoordinateSerializer {
    public static String serialize(Coordinate c) {
        return c.getX() + "," + c.getY();
    }

    public static Coordinate deserialize(String serialized) {
        String split[] = serialized.split(",");
        int x = Integer.parseInt(split[0]);
        int y = Integer.parseInt(split[1]);
        return new Coordinate(x, y);
    }
}
