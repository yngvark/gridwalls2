package com.yngvark.gridwalls.micrfoservices.zombie_test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MaxMinGathererTest {
    @Test
    public void should_gather_min_and_max() {
        MaxMinGatherer gatherer = new MaxMinGatherer();
        gatherer.add("x", 4);
        gatherer.add("x", 5);
        gatherer.add("x", 6);

        gatherer.add("y", -4);
        gatherer.add("y", 5);
        gatherer.add("y", 60);

        assertEquals(4, gatherer.min("x"));
        assertEquals(6, gatherer.max("x"));

        assertEquals(-4, gatherer.min("y"));
        assertEquals(60, gatherer.max("y"));
    }
}