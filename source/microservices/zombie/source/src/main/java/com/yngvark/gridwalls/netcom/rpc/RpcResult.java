package com.yngvark.gridwalls.netcom.rpc;

public interface RpcResult {
    boolean succeeded();
    String getFailedInfo();
    String getRpcResponse();
}
