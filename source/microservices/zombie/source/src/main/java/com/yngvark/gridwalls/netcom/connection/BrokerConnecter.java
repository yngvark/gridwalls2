package com.yngvark.gridwalls.netcom.connection;

import com.yngvark.gridwalls.netcom.connection.connect_status.ConnectionStatus;

public interface BrokerConnecter<T extends ConnectionWrapper> {
    ConnectionStatus<T> connect(String host, int timeoutMilliseconds);
}
