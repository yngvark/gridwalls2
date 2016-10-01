package com.yngvark.gridwalls.microservices.zombie.netcom;

import com.yngvark.gridwalls.microservices.zombie.gamelogic.GameConfig;

public class FetchGameConfigRpc {
    private static final String RPC_QUEUE_NAME = "rpc_queue";

    private final Broker broker;
    private final GameConfigDeserializer gameConfigDeserializer;

    public FetchGameConfigRpc(Broker broker, GameConfigDeserializer gameConfigDeserializer) {
        this.broker = broker;
        this.gameConfigDeserializer = gameConfigDeserializer;
    }

    public GameConfig getGameConfigFromServer() throws RpcException {
        String getGameConfigReponse = broker.rpcCall(RPC_QUEUE_NAME, "getGameConfig");
        return gameConfigDeserializer.deserialize(getGameConfigReponse);
    }
}
