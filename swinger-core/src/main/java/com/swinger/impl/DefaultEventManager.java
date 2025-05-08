package com.swinger.impl;

import com.swinger.api.EventListener;
import com.swinger.api.EventManager;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class DefaultEventManager implements EventManager {
    private final Map<String, Set<EventListener>> listenerMap = new ConcurrentHashMap<>();

    @Override
    public void subscribe(String event, EventListener listener) {
        log.info("subscribe {}", event);
        Set<EventListener> listenerSet = listenerMap.computeIfAbsent(event, k -> ConcurrentHashMap.newKeySet());
        listenerSet.add(listener);
    }

    @Override
    public boolean unsubscribe(String event, EventListener listener) {
        log.info("unsubscribe {}", event);
        Set<EventListener> listenerSet = listenerMap.computeIfAbsent(event, k -> ConcurrentHashMap.newKeySet());
        return listenerSet.remove(listener);
    }

    @Override
    public void publish(String event) {
        log.info("publish {}", event);
        Set<EventListener> listenerSet = listenerMap.computeIfAbsent(event, k -> ConcurrentHashMap.newKeySet());
        listenerSet.forEach(l -> l.onEvent(event));
    }
}