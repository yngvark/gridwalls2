package com.yngvark.gridwalls.netcom.rpc;

import com.yngvark.gridwalls.netcom.rpc.RpcResult;

public class RpcSucceeded implements RpcResult {
    private final String rpcResponse;

    public RpcSucceeded(String rpcResponse) {
        this.rpcResponse = rpcResponse;
    }

    @Override
    public boolean success() {
        return true;
    }

    @Override
    public boolean failed() {
        return false;
    }

    @Override
    public String getFailedInfo() {
        return "No failure info for a succeeded RPC.";
    }

    @Override
    public String getRpcResponse() {
        return rpcResponse;
    }
}
