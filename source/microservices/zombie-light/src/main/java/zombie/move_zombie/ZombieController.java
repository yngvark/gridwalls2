package zombie.move_zombie;

class ZombieController implements Zombie {
    private ZombieState zombie;

    public ZombieController(ZombieState zombie) {
        this.zombie = zombie;
    }

    @Override
    public Move move() {
        Container container = zombie.move();
        zombie = container.getNewZombie();
        return container.getMove();
    }

//    @Override
//    public void manMoved(ManMove manMove) {
//
//    }
}
