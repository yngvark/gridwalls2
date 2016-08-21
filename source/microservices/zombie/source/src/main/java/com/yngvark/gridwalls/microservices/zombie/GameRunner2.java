package com.yngvark.gridwalls.microservices.zombie;

import com.google.inject.Inject;

import java.io.IOException;

public class GameRunner2 {
    private final ExitSignalAwareRunner exitSignalAwareRunner;
    private final SystemInputCommandExecutor systemInputCommandExecutor;
    private final AbortableConnecter abortableConnecter;
    private final AbortableGameConfigFetcher abortableGameConfigFetcher;
    private final GameLoop gameLoop;

    @Inject
    public GameRunner2(ExitSignalAwareRunner exitSignalAwareRunner, SystemInputCommandExecutor systemInputCommandExecutor,
            AbortableConnecter abortableConnecter, AbortableGameConfigFetcher abortableGameConfigFetcher,
            GameLoop gameLoop) {
        this.exitSignalAwareRunner = exitSignalAwareRunner;
        this.systemInputCommandExecutor = systemInputCommandExecutor;
        this.abortableConnecter = abortableConnecter;
        this.abortableGameConfigFetcher = abortableGameConfigFetcher;
        this.gameLoop = gameLoop;
    }

    public void run() {
        System.out.println("Start async game.");
        exitSignalAwareRunner.run(new ICanRunAndAbort() {
            @Override
            public void run() {


                // Step: Connect.
                System.out.println("Connecting to message bus.");
                ConnectResult connectResult = abortableConnecter.connect();
                if (!connectResult.isConnected()) {
                    System.out.println("Connecting failed. Details: " + connectResult.getConnectFailedDetails());
                    return;
                }
                System.out.println("Connected.");

                /*
                // Step: Get game configuration.
                GameConfig gameConfig = abortableGameConfigFetcher.fetchGameConfig();

                // Step: Run game.
                gameLoop.run(connectResult.getConnection());
                */


                try {
                    connectResult.getConnection().close();
                } catch (IOException e) {
                    System.out.println("Error occured while disconnecting.");
                    e.printStackTrace();
                }

                System.out.println("Connection closed.");
            }

            @Override
            public void startAborting() {
                abortableConnecter.startAborting();
                gameLoop.startAborting();
            }
        });

        System.out.println("Start sync read from input.");

    }
}
