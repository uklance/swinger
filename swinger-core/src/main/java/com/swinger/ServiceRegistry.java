package com.swinger;

public interface ServiceRegistry {
    <T> T get(Class<T> type);
    <T> T get(String name, Class<T> type);
}
