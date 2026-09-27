package com.swinger.impl;

import com.swinger.api.ComponentInstance;
import com.swinger.api.ComponentInstances;

import java.util.*;

public class DefaultComponentInstances implements ComponentInstances {
    private final Map<Object, List<ComponentInstance>> byId = new LinkedHashMap<>();

    public void add(ComponentInstance component) {
        List<ComponentInstance> group = byId.computeIfAbsent(component.getDefinition().getId(), k -> new ArrayList<>());
        group.add(component);
    }

    @Override
    public Collection<ComponentInstance> getAll() {
        return byId.values().stream().flatMap(List::stream).toList();
    }

    @Override
    public Collection<ComponentInstance> get(String id) {
        return byId.getOrDefault(id, Collections.emptyList());
    }

    @Override
    public ComponentInstance get(String id, String key) {
        return get(id).stream()
                .filter(c -> key.equals(c.getKey()))
                .findFirst()
                .orElse(null);
    }
}
