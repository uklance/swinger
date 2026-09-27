package com.swinger.api;

import java.util.Collection;

public interface ComponentInstances {
    Collection<ComponentInstance> getAll();
    Collection<ComponentInstance> get(String id);
    ComponentInstance get(String id, String key);
}
