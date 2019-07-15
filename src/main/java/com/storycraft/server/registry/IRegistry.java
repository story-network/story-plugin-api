package com.storycraft.server.registry;

import com.storycraft.StoryPlugin;

public interface IRegistry<T> {

    void add(int id, T item) throws Exception;

    void remove(int id) throws Exception;

    void initialize(StoryPlugin plugin);
    void preInitialize(StoryPlugin plugin);
    void unInitialize(StoryPlugin plugin);

    boolean contains(String name);
    boolean containsId(int id);

    int getId(T item);
    String getName(T item);

    T getByName(String name);
    T getById(int id);
}
