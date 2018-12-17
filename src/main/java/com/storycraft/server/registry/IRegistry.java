package com.storycraft.server.registry;

import com.storycraft.StoryPlugin;

public interface IRegistry<T> {

    void add(String name, T item) throws Exception;

    void initialize(StoryPlugin plugin);
    void preInitialize(StoryPlugin plugin);

    boolean contains(String name);

    int getId(T item);
    String getName(T item);

    T getByName(String name);
    T getById(int id);
}
