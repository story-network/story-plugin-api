package com.storycraft.core;

import com.storycraft.StoryPlugin;

public interface IMiniPlugin {
    void onLoad(StoryPlugin plugin);
    void onUnload(boolean reload);
}
