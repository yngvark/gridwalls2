package com.yngvark.gridwalls.microservices.zombie.game;

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
