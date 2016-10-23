package com.yngvark.gridwalls.netcom.connection;

import com.yngvark.gridwalls.netcom.connection.connect_status.ConnectStatus;

public interface BrokerConnecter<T extends ConnectionWrapper> {
    ConnectStatus<T> connect(String host, int timeoutMilliseconds);
}
