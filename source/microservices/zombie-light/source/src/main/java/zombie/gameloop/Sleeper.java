package zombie.gameloop;

import java.util.concurrent.TimeUnit;

public interface Sleeper {
    void sleep(TimeUnit timeUnit, long count);
}
