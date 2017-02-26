package com.yngvark;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class KafkaRunnerTest {
	@Test
	public void testHello() {
		assertEquals("hello", new Person().hello());
	}

    @Test
    public void produce() throws Exception {
        KafkaRunner.produce();
    }

    @Test
    public void consume() throws Exception {
        KafkaRunner.consumeOnce();
    }

    @Test
    public void consumeContinously() throws Exception {
        KafkaRunner.consumeContinously();
    }

}
