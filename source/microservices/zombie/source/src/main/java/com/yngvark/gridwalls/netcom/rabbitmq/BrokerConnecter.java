package com.yngvark.gridwalls.netcom.rabbitmq;

import com.yngvark.gridwalls.netcom.ConnectStatus;
import com.yngvark.gridwalls.netcom.ConnectionWrapper;

public interface BrokerConnecter<T extends ConnectionWrapper> {
    ConnectStatus<T> connect(String host, int timeoutMilliseconds);
}
