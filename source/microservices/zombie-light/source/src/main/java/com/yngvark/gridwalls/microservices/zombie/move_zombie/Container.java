package com.yngvark.gridwalls.microservices.zombie.move_zombie;

class Container {
    final Move move;
    final ZombieState newZombie;

    public Container(Move move, ZombieState newZombie) {
        this.move = move;
        this.newZombie = newZombie;
    }
}
