package com.yngvark.gridwalls.netcom.publish;

public class PublishFailed implements PublishResult {
    private final String failedInfo;

    public PublishFailed(String failedInfo) {
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
