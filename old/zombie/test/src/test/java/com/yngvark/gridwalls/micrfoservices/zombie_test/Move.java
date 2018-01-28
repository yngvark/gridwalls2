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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Move move = (Move) o;

        if (toX != move.toX)
            return false;
        return toY == move.toY;
    }

    @Override
    public int hashCode() {
        int result = toX;
        result = 31 * result + toY;
        return result;
    }
}
