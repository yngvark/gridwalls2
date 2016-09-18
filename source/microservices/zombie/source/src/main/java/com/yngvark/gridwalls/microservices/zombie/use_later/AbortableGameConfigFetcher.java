package com.yngvark.gridwalls.microservices.zombie.use_later;

import com.google.inject.Inject;
import com.yngvark.gridwalls.microservices.zombie.gamelogic.GameConfig;
import com.yngvark.gridwalls.microservices.zombie.infrastructure.ICanAbortOnSignal;
import com.yngvark.gridwalls.microservices.zombie.netcom.ConnectResult;
import com.yngvark.gridwalls.netcom.NetCom;

import java.util.concurrent.Future;

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
