package com.yngvark.gridwalls.netcom.publish;

public interface PublishResult {
    boolean succeeded();
    String getFailedInfo();
}
