package com.yngvark.gridwalls.core;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class MapDimensions {
    private final int width;
    private final int height;

    public MapDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
