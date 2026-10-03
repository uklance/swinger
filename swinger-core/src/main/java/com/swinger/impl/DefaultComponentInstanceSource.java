package com.swinger.impl;

import com.swinger.api.Binding;
import com.swinger.api.ComponentDefinition;
import com.swinger.api.ComponentDecorator;
import com.swinger.api.ComponentInstance;
import com.swinger.api.ComponentInstanceSource;
import com.swinger.api.ComponentInstances;
import com.swinger.api.PropertyBinding;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class DefaultComponentInstanceSource implements ComponentInstanceSource {
    private final List<ComponentDecorator> decorators;

    @Override
    public ComponentInstance create(
            ComponentDefinition definition,
            List<PropertyBinding> properties,
            Binding keyBinding,
            ComponentInstance rootInstance,
            ComponentInstances renderedChildren
    ) throws Exception {
        Object component = definition.getType().getDeclaredConstructor().newInstance();
        Object key = keyBinding == null ? null : keyBinding.get(component);
        DefaultComponentInstance instance = new DefaultComponentInstance(
                definition,
                component,
                key,
                rootInstance,
                renderedChildren
        );

        for (ComponentDecorator decorator : decorators) {
            decorator.decorate(component, instance);
        }
        for (PropertyBinding property : properties) {
            property.getDefinition().apply(instance, property.getBinding());
        }
        return instance;
    }
}
