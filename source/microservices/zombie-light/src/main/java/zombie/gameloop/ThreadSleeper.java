package zombie.gameloop;

import java.util.concurrent.TimeUnit;

class ThreadSleeper implements Sleeper {
    @Override
    public void sleep(TimeUnit timeUnit, long count) {
        try {
            Thread.sleep(timeUnit.toMillis(count));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
