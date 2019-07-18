package com.storycraft.util;

public class Bindable<T> extends EventSet<T> implements IBindable<T> {

    private T value;

    public Bindable(T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;

        invoke(value);
    }
}