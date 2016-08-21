package com.yngvark.gridwalls.microservices.zombie;

import com.google.inject.Inject;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AbortableConnecter implements ICanAbortOnSignal {
    private final ExecutorService executor;
    private final MessageBusConnecter messageBusConnecter;

    private Future<ConnectResult> connectResultFuture;

    @Inject
    public AbortableConnecter(ExecutorService executor, MessageBusConnecter messageBusConnecter) {
        this.executor = executor;
        this.messageBusConnecter = messageBusConnecter;
    }

    public ConnectResult connect() {
        int connectTimeoutMilliseconds = 4000;

        connectResultFuture = executor.submit(() -> messageBusConnecter.connect(connectTimeoutMilliseconds));

        try {
            return connectResultFuture.get(connectTimeoutMilliseconds + 4000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            return new ConnectFailed("Connecting failed: Connecting aborted before succesfully connecting. Details: " + ExceptionUtils.getStackTrace(e));
        } catch (ExecutionException e) {
            return new ConnectFailed("Connecting failed of unknown reason. Details: " + ExceptionUtils.getStackTrace(e));
        } catch (TimeoutException e) {
            return new ConnectFailed("Connecting failed: Connecting timed out. Details:" + ExceptionUtils.getStackTrace(e));
        }
    }

    @Override
    public void startAborting() {
        if (connectResultFuture != null)
            connectResultFuture.cancel(true);
    }
}
