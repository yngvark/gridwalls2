package com.yngvark.gridwalls.netcom.connection.connect_status;

import com.yngvark.gridwalls.netcom.connection.ConnectionWrapper;

public interface ConnectStatus<T extends ConnectionWrapper> {
    boolean succeeded();
    boolean failed();
    String getConnectFailedDetails();
    T getConnectionWrapper();
}
