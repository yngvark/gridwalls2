package com.yngvark.gridwalls.netcom_forwarder_test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class Lock {
    private BlockingQueue blockingQueue = new LinkedBlockingQueue();

    public void waitForUnlock() {
        try {
            blockingQueue.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void unlock(String comment) {
        try {
            blockingQueue.put(comment);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
