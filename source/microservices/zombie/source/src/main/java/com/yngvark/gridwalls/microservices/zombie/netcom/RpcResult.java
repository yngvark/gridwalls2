package com.yngvark.gridwalls.microservices.zombie.netcom;

public interface RpcResult {
    boolean success();
    boolean failed();
    String getFailedInfo();
    String getRpcResponse();
}
