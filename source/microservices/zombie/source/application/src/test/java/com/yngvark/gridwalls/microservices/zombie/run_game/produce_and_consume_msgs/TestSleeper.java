package com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs;

import com.yngvark.gridwalls.microservices.zombie.run_game.Sleeper;

class TestSleeper implements Sleeper {
    private long millis;

    @Override
    public void sleep(long millis) {
        this.millis = millis;
    }

    public boolean lastSleepDurationWasBetweenInclusive(long millisMin, long millisMax) {
        return millis >= millisMin && millis <= millisMax;
    }


}
