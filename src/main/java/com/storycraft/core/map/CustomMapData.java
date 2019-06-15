package com.storycraft.core.map;

import com.storycraft.core.map.render.IMapRenderer;

import org.bukkit.map.MapCursorCollection;

public class CustomMapData {

    private MapScale scale;
    
    private IMapRenderer renderer;

    private MapCursorCollection cursorCollection;

    private boolean shouldTrack;
    private boolean locked;

    public CustomMapData(IMapRenderer renderer) {
        this(MapScale.ORIGINAL, false, false, renderer);
    }

    public CustomMapData(MapScale scale, IMapRenderer renderer) {
        this(scale, false, false, renderer);
    }

    public CustomMapData(MapScale scale, boolean shouldTrack, IMapRenderer renderer) {
       this(scale, shouldTrack, false, renderer);
    }

    public CustomMapData(MapScale scale, boolean shouldTrack, boolean locked, IMapRenderer renderer) {
        this.scale = scale;
        this.shouldTrack = shouldTrack;
        this.locked = locked;

        this.renderer = renderer;

        cursorCollection = new MapCursorCollection();
    }

    public MapScale getScale() {
        return scale;
    }

    public IMapRenderer getRenderer() {
        return renderer;
    }

    public MapCursorCollection getCursorCollection() {
        return cursorCollection;
    }

    public boolean getShouldTrack() {
        return shouldTrack;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setScale(MapScale scale) {
        this.scale = scale;
    }

    public void setShouldTrack(boolean shouldTrack) {
        this.shouldTrack = shouldTrack;
    }

    public void setRenderer(IMapRenderer renderer) {
        this.renderer = renderer;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public enum MapScale {
        MINI((byte) 0x0),
        SMALL((byte) 0x1),
        MEDIUM((byte) 0x2),
        LARGE((byte) 0x3),
        ORIGINAL((byte) 0x4);

        private byte byteSize;
        MapScale(byte byteSize) {
            this.byteSize = byteSize;
        }

        public byte getByteSize() {
            return byteSize;
        }

        public int getSizeX() {
            return 256;
        }

        public int getSizeY() {
            return 256;
        }

    }

}