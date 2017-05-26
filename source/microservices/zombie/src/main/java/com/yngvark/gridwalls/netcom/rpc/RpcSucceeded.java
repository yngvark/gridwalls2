package com.yngvark.gridwalls.netcom.rpc;

public class RpcSucceeded implements RpcResult {
    private final String rpcResponse;

    public RpcSucceeded(String rpcResponse) {
        this.rpcResponse = rpcResponse;
    }

    @Override
    public boolean succeeded() {
        return true;
    }

    @Override
    public String getFailedInfo() {
        return "No failure info for a connected RPC.";
    }

    @Override
    public String getRpcResponse() {
        return rpcResponse;
    }
}
