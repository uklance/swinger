package com.swinger.api;

public interface Registry {
    <T> T get(Class<T> type);
}
