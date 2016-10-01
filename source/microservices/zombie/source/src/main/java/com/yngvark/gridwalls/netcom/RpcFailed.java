package com.yngvark.gridwalls.netcom;

public class RpcFailed implements RpcResult {
    private final String failReason;

    public RpcFailed(String failReason) {
        this.failReason = failReason;
    }

    @Override
    public boolean success() {
        return false;
    }

    @Override
    public boolean failed() {
        return true;
    }

    @Override
    public String getFailedInfo() {
        return failReason;
    }

    @Override
    public String getRpcResponse() {
        return "";
    }
}
