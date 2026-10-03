package com.swinger.impl;

import com.swinger.api.*;

import java.util.Collection;
import java.util.List;

public class DefaultComponentInstance implements ComponentInstance {
    private final ComponentDefinition definition;
    private final Object instance;
    private final Object key;
    private final ComponentInstance rootInstance;
    private final ComponentInstances renderedChildren;

    public DefaultComponentInstance(
            ComponentDefinition definition,
            List<PropertyBinding> properties,
            Binding keyBinding, ComponentInstance rootInstance,
            ComponentInstances renderedChildren
    ) throws Exception {
        this.definition = definition;
        this.instance = definition.getType().getDeclaredConstructor().newInstance();
        this.rootInstance = rootInstance == null ? this : rootInstance;
        for (PropertyBinding property : properties) {
            property.getDefinition().apply(this, property.getBinding());
        }
        this.key = keyBinding == null ? null : keyBinding.get(instance);
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
        return rootInstance;
    }
}
