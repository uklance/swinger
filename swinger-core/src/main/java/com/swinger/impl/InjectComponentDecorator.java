package com.swinger.impl;

import com.swinger.api.ComponentDecorator;
import com.swinger.api.ComponentDefinition;
import com.swinger.api.ComponentInstance;
import com.swinger.api.Registry;
import lombok.AllArgsConstructor;

import javax.inject.Inject;
import java.lang.reflect.Field;

@AllArgsConstructor
public class InjectComponentDecorator implements ComponentDecorator {
    private final Registry registry;

    @Override
    public boolean supportsField(ComponentDefinition definition, Field field) {
        return field.isAnnotationPresent(Inject.class);
    }

    @Override
    public void decorateField(ComponentInstance componentInstance, Field field) throws Exception {
        Object value;
        if (field.getType() == ComponentInstance.class) {
            value = componentInstance;
        } else if (registry.contains(field.getType())) {
            value = registry.get(field.getType());
        } else {
            String msg = String.format("Cannot inject field %s of type %s", field.getName(), field.getType().getName());
            throw new IllegalArgumentException(msg);
        }
        field.set(componentInstance.getComponent(), value);
    }
}
