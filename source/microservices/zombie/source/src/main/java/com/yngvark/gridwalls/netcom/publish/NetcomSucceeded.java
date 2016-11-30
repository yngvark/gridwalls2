package com.yngvark.gridwalls.netcom.publish;

public class NetcomSucceeded implements NetcomResult {
    @Override
    public boolean succeeded() {
        return true;
    }

    @Override
    public String getFailedInfo() {
        return "No failure info available for succeeded publish.";
    }
}
