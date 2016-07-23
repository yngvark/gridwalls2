package com.yngvark.netcom;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class TestConnectionTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void should_throw_error_if_there_are_no_more_events() {
        TestConnection netCom = new TestConnection();
        Topic topic = netCom.subscribeTo("test");
        expectedException.expect(RuntimeException.class);
        topic.consume();
    }

    @Test
    public void should_consume_published_event() {
        TestConnection netCom = new TestConnection();
        Topic topic = netCom.subscribeTo("test");
        topic.publish("hei");
        assertEquals("hei", topic.consume());
    }
}


