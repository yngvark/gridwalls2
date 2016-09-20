package com.yngvark.gridwalls.microservices.zombie.commands;

import com.yngvark.gridwalls.microservices.zombie.netcom.BrokerConnecter;
import com.yngvark.gridwalls.microservices.zombie.netcom.ConnectResult;

public class Connect implements Command {
    private final BrokerConnecter brokerConnecter;

    private boolean connected = false;
    private Object connectedLock = new Object();

    public Connect(BrokerConnecter brokerConnecter) {
        this.brokerConnecter = brokerConnecter;
    }

    @Override
    public void run() {
        System.out.println("Usage: Connect to <broker host>");
    }

    @Override
    public void run(String[] arguments) {
        if (arguments.length == 0 || arguments.length > 1) {
            System.out.println("Usage: Connect to <broker host>");
            return;
        }

        synchronized (connectedLock) {
            if (connected) {
                System.out.println("Already connected.");
                return;
            }
        }

        String host = arguments[0];
        int timeoutMilliseconds = 5000;
        ConnectResult connectResult = brokerConnecter.connect(host, timeoutMilliseconds);

        if (!connectResult.success()) {
            System.out.println("Connect failed.");
        }

        synchronized (connectedLock) {
            connected = true;
            System.out.println("Connected.");
        }
    }
}
