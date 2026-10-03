package com.swinger.impl;

import com.swinger.api.ComponentDecorator;
import com.swinger.api.ComponentInstance;

import javax.inject.Inject;
import java.lang.reflect.Field;

public class InjectComponentDecorator implements ComponentDecorator {
    @Override
    public void decorate(Object component, ComponentInstance componentInstance) throws Exception {
        for (Class<?> current = component.getClass(); current != null; current = current.getSuperclass()) {
            for (Field field : current.getDeclaredFields()) {
                if (!field.isAnnotationPresent(Inject.class)) {
                    continue;
                }
                if (field.getType() != ComponentInstance.class) {
                    throw new IllegalArgumentException(
                            "Only @Inject fields of type ComponentInstance are supported: " + field
                    );
                }
                field.setAccessible(true);
                field.set(component, componentInstance);
            }
        }
    }
}
