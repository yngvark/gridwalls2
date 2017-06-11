package com.yngvark.gridwalls.microservices.zombie.run_game;

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
