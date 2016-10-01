package com.yngvark.gridwalls.netcom;

import com.yngvark.gridwalls.microservices.zombie.utils.MessageFormatter;

public class ConnectFailedFactory {
    private final MessageFormatter messageFormatter;

    public ConnectFailedFactory(MessageFormatter messageFormatter) {
        this.messageFormatter = messageFormatter;
    }

    public ConnectSucceeded succeeded(ConnectionWrapper connectionWrapper) {
        return new ConnectSucceeded(connectionWrapper);
    }

    public ConnectFailed failed(String reason) {
        return new ConnectFailed(messageFormatter, reason);
    }
}
