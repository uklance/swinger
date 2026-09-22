package com.swinger.api;

public interface Binding {
    Object get(ComponentInstance instance) throws Exception;

    default void set(ComponentInstance instance, Object value) throws Exception {
        throw new UnsupportedOperationException();
    }
}
