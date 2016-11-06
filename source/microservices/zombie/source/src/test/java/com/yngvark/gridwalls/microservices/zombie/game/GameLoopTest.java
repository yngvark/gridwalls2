package com.yngvark.gridwalls.microservices.zombie.game;

import com.yngvark.gridwalls.microservices.zombie.game.utils.Sleeper;
import com.yngvark.gridwalls.netcom.gameconfig.GameConfig;
import org.junit.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

public class GameLoopTest {
    @Test
    public void stop_should_return_when_looping_is_done() throws Exception {
        // Given
        ZombiesController zombiesController = mock(ZombiesController.class);
        BlockingQueue blockingQueue = spy(LinkedBlockingQueue.class);

        GameLoop gameLoop = new GameLoop(zombiesController, null, new Sleeper(100), blockingQueue);

        doCallRealMethod().when(blockingQueue).poll(any(Long.class), any(TimeUnit.class));

        doAnswer((invocationOnMock -> {
            System.out.println("Game loop has exited");
            return invocationOnMock.callRealMethod();
        })).when(blockingQueue).put(any());

        ExecutorService executorService = Executors.newCachedThreadPool();
        Future gameLoopFuture = executorService.submit(() -> gameLoop.run(GameConfig.builder().build()));

        // When
        gameLoop.stopLoopAndWaitUntilItCompletes();

        // Then
        System.out.println("Doing assert.");
        verify(blockingQueue).put(any());
        gameLoopFuture.get(1, TimeUnit.MILLISECONDS);
    }
}