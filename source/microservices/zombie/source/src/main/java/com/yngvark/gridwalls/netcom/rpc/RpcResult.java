package com.yngvark.gridwalls.netcom.rpc;

public interface RpcResult {
    boolean success();
    boolean failed();
    String getFailedInfo();
    String getRpcResponse();
}
