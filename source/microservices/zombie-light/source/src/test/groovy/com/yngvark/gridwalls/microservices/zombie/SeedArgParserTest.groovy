package com.yngvark.gridwalls.microservices.zombie

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*

class SeedArgParserTest {
    @Test
    void should_parse_seed_arg() {
        // Given
        String[] args = [ "whatever", "whatever2", "--nosleep", "-seed=89198" ]

        // When
        Random r = SeedArgParser.createRandom(args)

        // Then
        assertEquals(-1790012134, r.nextInt())
        assertEquals(1856907795, r.nextInt())
        assertEquals(-1293783061, r.nextInt())
        assertEquals(-2073372903, r.nextInt())
        assertEquals(-398205505, r.nextInt())
    }
}
