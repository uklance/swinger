package com.swinger.api;

public interface Binding {
    Object get() throws Exception;

    default void set(Object value) throws Exception {
        throw new UnsupportedOperationException();
    }
}
