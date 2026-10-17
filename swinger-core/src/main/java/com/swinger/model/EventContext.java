package com.swinger.model;

public interface EventContext {
    String getEvent();
    Object getContext();
    default <T> T getContext(Class<T> type) {
        return type.cast(getContext());
    }
}
