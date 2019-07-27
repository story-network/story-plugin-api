package com.storycraft.server.registry;

import com.storycraft.MainPlugin;

public interface IRegistry<T> {

    void add(int id, T item) throws Exception;

    void remove(int id) throws Exception;

    void initialize(MainPlugin plugin);
    void preInitialize(MainPlugin plugin);
    void unInitialize(MainPlugin plugin);

    boolean contains(String name);
    boolean containsId(int id);

    int getId(T item);
    String getName(T item);

    T getByName(String name);
    T getById(int id);
}
