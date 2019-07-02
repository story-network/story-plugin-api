package com.storycraft.core.map.render;

import java.util.Collection;

public interface IMapRenderer {

    boolean needRender();

    Collection<OffsetArea> getDirtyArea();

    void clearDirtyArea();

    byte[] render(OffsetArea area);

}