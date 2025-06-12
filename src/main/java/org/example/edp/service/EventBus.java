package org.example.edp.service;

import org.example.edp.event.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class EventBus {

    private static final EventBus INSTANCE = new EventBus();


    private final List<Object> listeners = new CopyOnWriteArrayList<>();

    private EventBus() {
    }

    public static EventBus getInstance() {
        return INSTANCE;
    }

    public void register(Object listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
            System.out.println("Registered listener: " + listener.getClass().getSimpleName());
        }
    }


    public void unregister(Object listener) {
        listeners.remove(listener);
        System.out.println("Unregistered listener: " + listener.getClass().getSimpleName());
    }

    public void post(Object event) {
        for (Object listener : listeners) {
            for (java.lang.reflect.Method method : listener.getClass().getMethods()) {
                if (method.isAnnotationPresent(Subscribe.class) && method.getParameterCount() == 1 &&
                        method.getParameterTypes()[0].isInstance(event)) {
                    try {
                        method.invoke(listener, event);
                    } catch (Exception e) {
                        System.err.println("Error invoking event listener method: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}