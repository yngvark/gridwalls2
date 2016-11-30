package com.yngvark.gridwalls.netcom.publish;

public class NetcomFailed implements NetcomResult {
    private final String failedInfo;

    public NetcomFailed(String failedInfo) {
        this.failedInfo = failedInfo;
    }

    @Override
    public boolean succeeded() {
        return false;
    }

    @Override
    public String getFailedInfo() {
        return failedInfo;
    }
}
