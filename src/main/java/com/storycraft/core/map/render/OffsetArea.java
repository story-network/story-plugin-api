package com.storycraft.core.map.render;

public class OffsetArea {

    int x,y;
    int sizeX, sizeY;

    public OffsetArea() {
        this(0, 0);
    }

    public OffsetArea(int x, int y) {
        this(0, 0, 0, 0);
    }

    public OffsetArea(int x, int y, int sizeX, int sizeY) {
        this.x = x;
        this.y = y;

        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

}