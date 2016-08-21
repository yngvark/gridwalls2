package com.yngvark.gridwalls.test_utils;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SyncronousExecutorService implements ExecutorService {
    @Override
    public void shutdown() {
        throw new RuntimeException("Not implemented.");
    }

    @Override
    public List<Runnable> shutdownNow() {
        throw new RuntimeException("Not implemented.");
    }

    @Override
    public boolean isShutdown() {
        throw new RuntimeException("Not implemented.");
    }

    @Override
    public boolean isTerminated() {
        throw new RuntimeException("Not implemented.");
    }

    @Override
    public boolean awaitTermination(long l, TimeUnit timeUnit) throws InterruptedException {
        throw new RuntimeException("Not implemented.");
    }

    @Override
    public <T> Future<T> submit(Callable<T> callable) {
        throw new RuntimeException("Not implemented.");
    }

    @Override
    public <T> Future<T> submit(Runnable runnable, T t) {
        throw new RuntimeException("Not implemented.");
    }

    @Override
    public Future<?> submit(Runnable runnable) {
        runnable.run();
        return null;
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> collection) throws InterruptedException {
        throw new RuntimeException("Not implemented.");
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> collection, long l, TimeUnit timeUnit) throws InterruptedException {
        throw new RuntimeException("Not implemented.");
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> collection) throws InterruptedException, ExecutionException {
        throw new RuntimeException("Not implemented.");
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> collection, long l, TimeUnit timeUnit)
            throws InterruptedException, ExecutionException, TimeoutException {
        throw new RuntimeException("Not implemented.");
    }

    @Override
    public void execute(Runnable runnable) {
        throw new RuntimeException("Not implemented.");
    }
}
