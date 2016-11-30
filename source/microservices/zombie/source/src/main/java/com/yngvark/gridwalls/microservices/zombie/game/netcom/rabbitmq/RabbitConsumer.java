package com.yngvark.gridwalls.microservices.zombie.game.netcom.rabbitmq;

import com.yngvark.gridwalls.netcom.consume.ConsumeHandler;
import com.yngvark.gridwalls.netcom.consume.Consumer;
import com.yngvark.gridwalls.netcom.publish.NetcomResult;

public class RabbitConsumer implements Consumer {
    @Override
    public NetcomResult startConsume(ConsumeHandler handler) {
        throw new RuntimeException("HYEY");
    }
}
