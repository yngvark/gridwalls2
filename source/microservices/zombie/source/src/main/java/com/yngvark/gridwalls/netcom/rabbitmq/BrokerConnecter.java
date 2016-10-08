package com.yngvark.gridwalls.netcom.rabbitmq;

import com.yngvark.gridwalls.netcom.ConnectAttempt;

public interface BrokerConnecter {
    ConnectAttempt connect(String host, int timeoutMilliseconds);
}
