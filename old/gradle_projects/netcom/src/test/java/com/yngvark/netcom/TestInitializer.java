package com.yngvark.netcom;

public class TestInitializer implements Initializer {
    private boolean connected = false;
    private Connection connection;

    @Override
    public void connect(String host) {
        if (host.isEmpty())
            throw new NoHostProvidedException("You must specify a hostname or IP.");

        connected = true;
        connection = new TestConnection();
    }

    public Connection getConnection() {
        if (!connected)
            throw new NotYetConnectedException("Connect to a host before calling this method.");

        return connection;
    }
}
