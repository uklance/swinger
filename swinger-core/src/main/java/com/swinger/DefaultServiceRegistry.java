package com.swinger;

import lombok.AllArgsConstructor;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@AllArgsConstructor
public class DefaultServiceRegistry implements ServiceRegistry {
    private final Map<String, Object> byName;
    private final Map<Class<?>, Object> byType;

    public DefaultServiceRegistry(Map<Class<?>, Object> byType) {
       this(Collections.emptyMap(), byType);
    }

    @Override
    public <T> T get(Class<T> type) {
        Object service = byType.get(type);
        Objects.requireNonNull(service, type.getName());
        return type.cast(service);
    }

    @Override
    public <T> T get(String name, Class<T> type) {
        Object service = byName.get(name);
        Objects.requireNonNull(service, name);
        return type.cast(service);
    }
}
