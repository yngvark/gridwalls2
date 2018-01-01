package com.yngvark.gridwalls.microservices.zombie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

class SeedArgParser {
    private static final Logger logger = LoggerFactory.getLogger(SeedArgParser.class);

    static Random createRandom(String[] args) {
        for (String arg : args) {
            if (arg.contains("-seed=")) {
                String seed = arg.substring(6);
                logger.info("Using random with seed {}", seed);
                return new Random(Integer.parseInt(seed));
            }
        }

        logger.info("Using random with random seed");
        return new Random();
    }
}
