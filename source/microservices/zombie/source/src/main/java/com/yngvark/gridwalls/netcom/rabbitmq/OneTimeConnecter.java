package com.yngvark.gridwalls.netcom.rabbitmq;

import com.yngvark.gridwalls.netcom.ConnectAttempt;

public interface OneTimeConnecter {
    ConnectAttempt connect(String host, int timeoutMilliseconds);
}
