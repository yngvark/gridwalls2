package zombie.move_zombie;

public class ZombieFactory {
    public static Zombie create() {
        return new ZombieController(new WanderingZombie(1, 1));
    }
}
