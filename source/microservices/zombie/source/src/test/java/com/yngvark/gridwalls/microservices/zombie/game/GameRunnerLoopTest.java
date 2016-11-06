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

public class GameRunnerLoopTest {
    @Test
    public void should_not_run_loop_if_loop_stopped_before_starting() throws Exception {
        // Given
        GameLoopFactory gameLoopFactory = mock(GameLoopFactory.class);
        BlockingQueue blockingQueue = spy(LinkedBlockingQueue.class);
        GameRunnerLoop gameRunnerLoop = new GameRunnerLoop(gameLoopFactory, blockingQueue);
        gameRunnerLoop.stopLoopAndWaitUntilItCompletes();

        // When
        gameRunnerLoop.run(null);

        // Then
        verify(gameLoopFactory, times(0)).create(any());
        verify(blockingQueue, times(0)).poll(any(Integer.class), any());
    }

    @Test
    public void stop_should_return_after_looping_is_done() throws Exception {
        // Given
        GameLoopFactory gameLoopFactory = mock(GameLoopFactory.class);
        GameLoop gameLoop = mock(GameLoop.class);
        when(gameLoopFactory.create(any())).thenReturn(gameLoop);

        BlockingQueue blockingQueue = spy(LinkedBlockingQueue.class);

        GameRunnerLoop gameRunnerLoop = new GameRunnerLoop(gameLoopFactory, blockingQueue);

        doCallRealMethod().when(blockingQueue).poll(any(Long.class), any(TimeUnit.class));

        doAnswer((invocationOnMock -> {
            System.out.println("Game loop has exited.");
            return invocationOnMock.callRealMethod();
        })).when(blockingQueue).put(any());

        BlockingQueue waitForLoopRun = new LinkedBlockingQueue();
        doAnswer(invocationOnMock -> {
            waitForLoopRun.put("Game loop has been run.");
            return Void.TYPE;
        }).when(gameLoop).loop();

        ExecutorService executorService = Executors.newCachedThreadPool();
        Future gameLoopFuture = executorService.submit(() -> gameRunnerLoop.run(GameConfig.builder().build()));
        Object called = waitForLoopRun.poll(3, TimeUnit.SECONDS);
        if (called == null) throw new RuntimeException("Sleeper was never called");

        // When
        gameRunnerLoop.stopLoopAndWaitUntilItCompletes();

        // Then
        System.out.println("Doing assert.");
        verify(blockingQueue).put(any());
        gameLoopFuture.get(1, TimeUnit.MILLISECONDS);
    }
}