package com.yngvark.gridwalls.netcom.publish;

public class PublishSucceeded implements PublishResult{
    @Override
    public boolean succeeded() {
        return true;
    }

    @Override
    public String getFailedInfo() {
        return "No failure info available for succeeded publish.";
    }
}
