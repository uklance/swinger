package com.swinger.impl;

import com.swinger.api.EventListener;
import com.swinger.api.EventManager;
import com.swinger.model.EventContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Collections;
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
        Set<EventListener> listenerSet = listenerMap.get(event);
        if (listenerSet != null) {
            return listenerSet.remove(listener);
        }
        return false;
    }

    @Override
    public void publish(String event, Object context) throws Exception {
        Set<EventListener> listenerSet = listenerMap.get(event);
        log.info("publish {}, {} to {} listeners", event, context, listenerSet == null ? 0 : listenerSet.size());
        if (listenerSet != null) {
            EventContext eventContext = new EventContext() {
                @Override
                public String getEvent() {
                    return event;
                }

                @Override
                public Object getContext() {
                    return context;
                }
            };
            for (EventListener listener : listenerSet) {
                listener.onEvent(eventContext);
            }
        }
    }
}