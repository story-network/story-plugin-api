package com.storycraft.util;

import java.util.ArrayList;
import java.util.List;

public class EventSet<A> {

    @FunctionalInterface
    public interface EventRunnable<A> {
        void on(A args);
    }

    private List<EventRunnable<A>> listenerList;

    public EventSet(){
        this.listenerList = new ArrayList<>();
    }

    public EventRunnable<A> add(EventRunnable<A> eventRunnable){
        listenerList.add(eventRunnable);

        return eventRunnable;
    }

    public boolean remove(EventRunnable<A> eventRunnable){
        return listenerList.remove(eventRunnable);
    }

    public void invoke(A args){
        for (EventRunnable eventRunnable : listenerList){
            eventRunnable.on(args);
        }
    }
}
