package com.swinger.model;

public interface EventContext {
    String getEvent();
    Object getContext();
    <T> T getContext(Class<T> type);
}
