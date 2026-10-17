package com.swinger.impl;

import com.swinger.annotation.OnEvent;
import com.swinger.api.ComponentDecorator;
import com.swinger.api.ComponentDefinition;
import com.swinger.api.ComponentInstance;
import com.swinger.api.EventManager;
import lombok.AllArgsConstructor;

import java.lang.reflect.Method;

@AllArgsConstructor
public class OnEventComponentDecorator implements ComponentDecorator {
    private final EventManager eventManager;

    @Override
    public boolean supportsMethod(ComponentDefinition definition, Method method) {
        if (method.isAnnotationPresent(OnEvent.class)) {
            if (method.getParameterCount() <= 1) {
                return true;
            }
            String msg = String.format("Method %s has %s parameter(s), expected 1 or 0", method.getName(), method.getParameterCount());
            throw new IllegalArgumentException(msg);
        }
        return false;
    }

    @Override
    public void decorateMethod(ComponentInstance componentInstance, Method method) {
        OnEvent onEvent = method.getAnnotation(OnEvent.class);
        eventManager.subscribe(onEvent.value(), context -> {
            if (method.getParameterCount() == 0) {
                method.invoke(componentInstance.getComponent());
            } else {
                method.invoke(componentInstance.getComponent(), context.getContext());
            }
        });
    }
}
