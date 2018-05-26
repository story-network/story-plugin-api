package com.storycraft.server.registry;

import com.storycraft.StoryPlugin;

public interface IRegistry<T> {

    void initialize(StoryPlugin plugin);

    boolean contains(T object);

    T getByName(String name);
    T getById(int id);
}
