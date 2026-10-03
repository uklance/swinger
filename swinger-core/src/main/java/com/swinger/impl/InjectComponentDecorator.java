package com.swinger.impl;

import com.swinger.api.ComponentDecorator;
import com.swinger.api.ComponentInstance;
import com.swinger.api.Registry;

import javax.inject.Inject;
import java.lang.reflect.Field;

public class InjectComponentDecorator implements ComponentDecorator {
    private final Registry registry;

    public InjectComponentDecorator() {
        this(null);
    }

    public InjectComponentDecorator(Registry registry) {
        this.registry = registry;
    }

    @Override
    public void decorate(Object component, ComponentInstance componentInstance) throws Exception {
        for (Class<?> current = component.getClass(); current != null; current = current.getSuperclass()) {
            for (Field field : current.getDeclaredFields()) {
                if (!field.isAnnotationPresent(Inject.class)) {
                    continue;
                }
                Object value;
                if (field.getType() == ComponentInstance.class) {
                    value = componentInstance;
                } else if (registry != null) {
                    value = registry.get(field.getType());
                } else {
                    throw new IllegalArgumentException(
                            "No Registry is available to inject field: " + field
                    );
                }
                field.setAccessible(true);
                field.set(component, value);
            }
        }
    }
}
