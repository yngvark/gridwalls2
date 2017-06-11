package com.yngvark.gridwalls.microservices.zombie.run_game.produce_and_consume_msgs;

public class ProducerContext {
    private Producer currentProducer;

    public ProducerContext(Producer currentProducer) {
        this.currentProducer = currentProducer;
    }

    public void setCurrentProducer(Producer currentProducer) {
        this.currentProducer = currentProducer;
    }

    public String nextMsg() {
        return currentProducer.nextMsg(this);
    }
}
