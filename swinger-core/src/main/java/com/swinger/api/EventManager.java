package com.swinger.api;

public interface EventManager {
    void subscribe(String event, EventListener listener);
    boolean unsubscribe(String event, EventListener listener);
    void publish(String event);
}