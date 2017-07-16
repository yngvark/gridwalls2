package com.yngvark.gridwalls.micrfoservices.zombie_test;

public class Move {
    public final int toX;
    public final int toY;

    public Move(int toX, int toY) {
        this.toX = toX;
        this.toY = toY;
    }

    @Override
    public String toString() {
        return "Move{" +
                "toX=" + toX +
                ", toY=" + toY +
                '}';
    }
}
