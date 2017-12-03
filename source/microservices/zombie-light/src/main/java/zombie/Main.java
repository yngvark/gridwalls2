package zombie;

import zombie.gameloop.GameLoopRunner;

public class Main {
    public static void main(String[] args) {
        GameLoopRunner gameLoopRunner = GameLoopRunner.create(null, null);
        gameLoopRunner.run();
    }
}
