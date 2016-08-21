package com.yngvark.gridwalls.microservices.zombie;

import com.google.inject.Inject;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class GameRunner3 {
    private final ExitSignalAwareRunner exitSignalAwareRunner;
    private final SystemInputCommandExecutor systemInputCommandExecutor;
    private final AbortableConnecter abortableConnecter;
    private final AbortableGameConfigFetcher abortableGameConfigFetcher;
    private final GameLoop gameLoop;
    private final Object exitProcessLock = new Object();

    @Inject
    public GameRunner3(ExitSignalAwareRunner exitSignalAwareRunner, SystemInputCommandExecutor systemInputCommandExecutor,
            AbortableConnecter abortableConnecter, AbortableGameConfigFetcher abortableGameConfigFetcher,
            GameLoop gameLoop) {
        this.exitSignalAwareRunner = exitSignalAwareRunner;
        this.systemInputCommandExecutor = systemInputCommandExecutor;
        this.abortableConnecter = abortableConnecter;
        this.abortableGameConfigFetcher = abortableGameConfigFetcher;
        this.gameLoop = gameLoop;
    }

    public void run() {
        System.out.println("Gamerunner running.");
        exitSignalAwareRunner.run(new ICanRunAndAbort() {
            @Override
            public void run() {
                System.out.println("Abortable gamerunner running.");
                systemInputCommandExecutor.readFromStdIn();
            }

            @Override
            public void startAborting() {
                System.out.println("GameRunner3 stopAborting");
                synchronized (exitProcessLock) {
                    exitProcessLock.notify();
                }
            }
        });

        //blockUntilGameExits();
        //systemInputCommandExecutor.exit();
        System.out.println("Exiting " + getClass().getSimpleName() + ".");
    }

    private void blockUntilGameExits() {
        System.out.println("Blocking until game exits...");
        try {
            synchronized (exitProcessLock) {
                exitProcessLock.wait();
            }
        } catch (InterruptedException e) {
            System.out.println("Exit process lock interruped: " + ExceptionUtils.getStackTrace(e));
        }
        System.out.println("Blocking until game exits... Done.");
    }


}
