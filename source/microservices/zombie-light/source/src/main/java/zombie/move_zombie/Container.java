package zombie.move_zombie;

class Container {
    private final Move move;
    private final ZombieState newZombie;

    public Container(Move move, ZombieState newZombie) {
        this.move = move;
        this.newZombie = newZombie;
    }

    public Move getMove() {
        return move;
    }

    public ZombieState getNewZombie() {
        return newZombie;
    }
}
