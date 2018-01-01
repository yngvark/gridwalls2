package com.yngvark.gridwalls.microservices.zombie.gameloop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

class ThreadSleeper implements Sleeper {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void sleep(TimeUnit timeUnit, long count) {
        try {
            long millisToSleep = timeUnit.toMillis(count);
            logger.info("Sleeping milliseconds: {}", millisToSleep);
            Thread.sleep(millisToSleep);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
