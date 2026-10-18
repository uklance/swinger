package com.swinger.impl;

import com.swinger.annotation.InjectComponents;
import com.swinger.api.ComponentDecorator;
import com.swinger.api.ComponentDefinition;
import com.swinger.api.ComponentInstance;
import com.swinger.api.ComponentsRef;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class InjectComponentsComponentDecorator implements ComponentDecorator {
    @Override
    public boolean supportsField(ComponentDefinition definition, Field field) {
        if (field.isAnnotationPresent(InjectComponents.class)) {
            if (field.getType() != ComponentsRef.class) {
                throw new IllegalArgumentException("Field annotated with @InjectComponents must be of type ComponentsRef");
            }
            return true;
        }
        return false;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void decorateField(ComponentInstance instance, Field field) throws Exception {
        InjectComponents annotation = field.getAnnotation(InjectComponents.class);
        String componentId = annotation.value();
        ComponentsRef componentsRef = new ComponentsRef() {
            @Override
            public Object get(Object key) {
                return instance.getRenderedChild(componentId, key);
            }

            @Override
            public Collection values() {
                return instance.getRenderedChildren(componentId).stream().map(ComponentInstance::getComponent).toList();
            }

            @Override
            public Set<Object> keySet() {
                return instance.getRenderedChildren(componentId).stream().map(ComponentInstance::getKey).collect(Collectors.toSet());
            }
        };
        field.set(instance.getComponent(), componentsRef);
    }
}
