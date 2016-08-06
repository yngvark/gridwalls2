package com.yngvark.gridwalls.microservices.zombie;

public interface ICanExitOnSignal {
    void exitSignalReceived() throws Exception;
}
