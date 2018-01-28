package com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs.move;

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
