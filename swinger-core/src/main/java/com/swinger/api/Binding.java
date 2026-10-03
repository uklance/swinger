package com.swinger.api;

public interface Binding {
    Object get(Object target) throws Exception;

    default void set(Object target, Object value) throws Exception {
        throw new UnsupportedOperationException();
    }
}
