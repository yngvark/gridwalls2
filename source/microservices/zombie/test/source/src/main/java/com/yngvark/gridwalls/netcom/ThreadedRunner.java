package com.yngvark.gridwalls.netcom;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadedRunner {
    private final ICanRunAndStop iCanRunAndStop;

    public ThreadedRunner(ICanRunAndStop iCanRunAndStop) {
        this.iCanRunAndStop = iCanRunAndStop;
    }

    public void runInNewThread() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            iCanRunAndStop.run();
        });
    }

    public void stop() {
        iCanRunAndStop.stop();
    }

}
