package com.swinger.api;

import java.util.Collection;

public interface ComponentInstance {
    ComponentDefinition getDefinition();
    Object getKey();
    Object getComponent();
    Collection<ComponentInstance> getRenderedChildren();
    Collection<ComponentInstance> getRenderedChildren(String id);
    ComponentInstance getRenderedChild(String id, String key);
    ComponentInstance getRootInstance();
    default Object getRootComponent() {
        return getRootInstance().getComponent();
    }
}
