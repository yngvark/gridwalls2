package com.yngvark.gridwalls.microservices.gameserver;

public interface RpcRequestHandler {
    String handle(String request);
}
