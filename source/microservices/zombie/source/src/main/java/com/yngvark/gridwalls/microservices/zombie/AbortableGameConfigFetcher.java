package com.yngvark.gridwalls.microservices.zombie;

import com.google.inject.Inject;
import com.yngvark.gridwalls.netcom.NetCom;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.sql.Time;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AbortableGameConfigFetcher implements ICanAbortOnSignal {
    private final NetCom netCom;
    private Future<ConnectResult> subscribeFuture;

    @Inject
    public AbortableGameConfigFetcher(NetCom netCom) {
        this.netCom = netCom;
    }

    public GameConfig fetchGameConfig() {
        // TODO: Subscribe til replyqueue. Send beskjed. Ventpå svar til replyQueue. Opprette GameConfig basert på svar.
/*

        int connectTimeoutMilliseconds = 4000;

        ExecutorService executor = Executors.newCachedThreadPool();

        String rpcReplyTopic = "rpcReplyTopic_" + UUID.randomUUID().toString();
        CompletableFuture subscribeFuture = netCom.subscribeTo(rpcReplyTopic);
        subscribeFuture.get(4, TimeUnit.SECONDS);



        this.subscribeFuture = executor.submit(() -> netCom.publish("[GetGameConfig] rpcReplyTopic=" + rpcReplyTopic));
*/

        return null;
    }

    @Override
    public void startAborting() {
        if (subscribeFuture != null)
            subscribeFuture.cancel(true);
    }
}
