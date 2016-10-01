package com.yngvark.gridwalls.netcom;

public interface RpcResult {
    boolean success();
    boolean failed();
    String getFailedInfo();
    String getRpcResponse();
}
