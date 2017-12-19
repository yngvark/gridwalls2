package com.yngvark.gridwalls.micrfoservices.zombie_test;

import java.util.HashMap;
import java.util.Map;

public class MaxMinGatherer {
    private final Map<String, MinMax> minMax = new HashMap<>();

    public void add(String name, Integer i) {
        if (minMax.get(name) == null)
            minMax.put(name, new MinMax(i, i));
        else {
            Integer max = Math.max(i, minMax.get(name).max);
            Integer min = Math.min(i, minMax.get(name).min);
            minMax.put(name, new MinMax(min, max));
        }
    }

    class MinMax {
        private final Integer min;
        private final Integer max;

        public MinMax(Integer min, Integer max) {
            this.min = min;
            this.max = max;
        }
    }

    public int min(String name) {
        return minMax.get(name).min;
    }

    public int max(String name) {
        return minMax.get(name).max;
    }
}
