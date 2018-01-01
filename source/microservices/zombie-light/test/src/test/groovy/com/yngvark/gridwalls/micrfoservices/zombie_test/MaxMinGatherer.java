package com.yngvark.gridwalls.micrfoservices.zombie_test;

import java.util.HashMap;
import java.util.Map;

class MaxMinGatherer {
    private final Map<String, MinMax> minMax = new HashMap<>();

    public void add(String name, Integer newValue) {
        if (minMax.get(name) == null)
            minMax.put(name, new MinMax(newValue, newValue));
        else {
            Integer max = Math.max(newValue, minMax.get(name).max);
            Integer min = Math.min(newValue, minMax.get(name).min);
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
