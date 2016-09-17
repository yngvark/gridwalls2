package com.yngvark.gridwalls.microservices.zombie.netcom;

import com.yngvark.gridwalls.microservices.zombie.netcom.ConnectResult;

public interface MessageBusConnecter {
    ConnectResult connect(int timeoutMilliseconds);
}
