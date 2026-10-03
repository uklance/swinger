package com.swinger.impl;

import com.swinger.api.*;

import java.util.Collection;

public class DefaultComponentInstance implements ComponentInstance {
    private final ComponentDefinition definition;
    private final Object instance;
    private final Object key;
    private final ComponentInstance rootInstance;
    private final ComponentInstances renderedChildren;

    public DefaultComponentInstance(
            ComponentDefinition definition,
            Object instance,
            Object key,
            ComponentInstance rootInstance,
            ComponentInstances renderedChildren
    ) {
        this.definition = definition;
        this.instance = instance;
        this.key = key;
        this.rootInstance = rootInstance;
        this.renderedChildren = renderedChildren;
    }

    @Override
    public ComponentDefinition getDefinition() {
        return definition;
    }

    @Override
    public Object getKey() {
        return key;
    }

    @Override
    public Object getComponent() {
        return instance;
    }

    @Override
    public Collection<ComponentInstance> getRenderedChildren() {
        return renderedChildren.getAll();
    }

    @Override
    public Collection<ComponentInstance> getRenderedChildren(String id) {
        return renderedChildren.get(id);
    }

    @Override
    public ComponentInstance getRenderedChild(String id, String key) {
        return renderedChildren.get(id, key);
    }

    @Override
    public ComponentInstance getRootInstance() {
        return rootInstance == null ? this : rootInstance;
    }
}
