package com.yngvark.gridwalls.netcom.rabbitmq;

import com.yngvark.gridwalls.netcom.ConnectStatus;

public interface BrokerConnecter {
    ConnectStatus connect(String host, int timeoutMilliseconds);
}
