package com.yngvark.gridwalls.netcom.rabbitmq;

import com.rabbitmq.client.Connection;
import com.yngvark.gridwalls.microservices.zombie.infrastructure.StackTracePrinter;

public class RabbitConnectionWrapperFactory {
    private final StackTracePrinter stackTracePrinter;

    public RabbitConnectionWrapperFactory(StackTracePrinter stackTracePrinter) {
        this.stackTracePrinter = stackTracePrinter;
    }

    public RabbitConnectionWrapper create(Connection connection) {
        return new RabbitConnectionWrapper(stackTracePrinter, connection);
    }
}
