package com.swinger.impl;

import com.swinger.annotation.InjectComponents;
import com.swinger.api.ComponentDecorator;
import com.swinger.api.ComponentDefinition;
import com.swinger.api.ComponentInstance;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class InjectComponentsComponentDecorator implements ComponentDecorator {
    @Override
    public boolean supportsField(ComponentDefinition definition, Field field) {
        if (field.isAnnotationPresent(InjectComponents.class)) {
            if (field.getType() != Map.class) {
                throw new IllegalArgumentException("Field annotated with @InjectComponents must be of type Map");
            }
            return true;
        }
        return false;
    }

    @Override
    public void decorateField(ComponentInstance componentInstance, Field field) throws Exception {
        InjectComponents annotation = field.getAnnotation(InjectComponents.class);
        String componentId = annotation.value();
        Collection<ComponentInstance> instances = componentInstance.getRenderedChildren(componentId);
        Map<Object, Object> map = instances.stream().collect(toMap(ComponentInstance::getKey, ComponentInstance::getComponent));
        field.set(componentInstance.getComponent(), map);
    }
}
