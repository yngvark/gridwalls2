package com.yngvark.gridwalls.microservices.zombie.netcom;

public interface OneTImeConnecter {
    ConnectAttempt connect(String host, int timeoutMilliseconds);
}
