package zombie.move_zombie;

class WanderingZombie implements ZombieState {
    private final int x;
    private final int y;

    public WanderingZombie(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Container move() {
        return new Container(
                new Move(1, 1),
                new WanderingZombie(1, 1)
        );
    }

//    Zombie notice(ManMove manMove) {
//        return new ZombieThatHasNoticedAMan(manMove);
//    }

    // Attack attack()
}
