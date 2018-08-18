package com.storycraft.util;

import java.util.concurrent.*;

public class AsyncTask<T> {

    private final static ExecutorService executor;

    static {
        executor = Executors.newCachedThreadPool();
    }

    private AsyncCallable<T> supplier;
    private CompletableFuture<T> task;
    private AsyncNext<T> onComplete;

    public AsyncTask(AsyncCallable<T> supplier) {
        this.supplier = supplier;
    }

    public void run() {
        this.task = CompletableFuture.supplyAsync(this::runTask, executor);
    }

    private T runTask() {
        Throwable throwable = null;
        T result = null;

        try {
            result = supplier.get();
        } catch (Throwable t) {
            throwable = t;
        }

        if (onComplete != null) {
            onComplete.then(result, throwable);
        }
        else if (throwable != null) {
            throwable.printStackTrace();
        }

        return result;
    }

    public void then(AsyncNext<T> onComplete) {
        this.onComplete = onComplete;
    }

    public T getSync() throws Throwable {
        return supplier.get();
    }

    public static abstract class AsyncCallable<T> implements AsyncSupplier<T> {

        public <A>A await(AsyncTask<A> task) throws Throwable {
            return task.task.join();
        }
    }

    @FunctionalInterface
    protected interface AsyncSupplier<T> {
        T get() throws Throwable;
    }

    @FunctionalInterface
    public interface AsyncNext<T> {
        void then(T result, Throwable throwable);
    }
}
