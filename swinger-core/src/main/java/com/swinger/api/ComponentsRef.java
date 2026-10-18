package com.swinger.api;

import java.util.Collection;
import java.util.Set;

public interface ComponentsRef<Long, T> {
    Set<Object> keySet();
    Collection<T> values();
    T get(Long key);
}
