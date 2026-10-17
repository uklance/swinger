package com.swinger.impl;

import com.swinger.api.Binding;
import com.swinger.api.ComponentDefinition;
import com.swinger.api.ComponentDecorator;
import com.swinger.api.ComponentInstance;
import com.swinger.api.ComponentInstanceSource;
import com.swinger.api.ComponentInstances;
import com.swinger.api.PropertyBinding;
import lombok.AllArgsConstructor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@AllArgsConstructor
public class DefaultComponentInstanceSource implements ComponentInstanceSource {
    private final List<ComponentDecorator> decorators;
    private final Map<Class<?>, UberDecorator> uberDecorators = new ConcurrentHashMap<>();

    private interface UberDecorator {
        void decorate(ComponentInstance instance) throws Exception;
    }

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

        UberDecorator uberDecorator = uberDecorators.computeIfAbsent(definition.getType(), k -> createUberDecorator(definition));
        uberDecorator.decorate(instance);
        for (PropertyBinding property : properties) {
            property.getDefinition().apply(instance, property.getBinding());
        }
        return instance;
    }

    private UberDecorator createUberDecorator(ComponentDefinition definition) {
        Map<Field, List<ComponentDecorator>> fieldDecorators = new LinkedHashMap<>();
        Map<Method, List<ComponentDecorator>> methodDecorators = new LinkedHashMap<>();
        for (Class<?> type = definition.getType(); type != null; type = type.getSuperclass()) {
            for (Field field : type.getDeclaredFields()) {
                for (ComponentDecorator decorator : decorators) {
                    if (decorator.supportsField(definition, field)) {
                        fieldDecorators.computeIfAbsent(field, k -> new ArrayList<>()).add(decorator);
                    }
                }
            }
            for (Method method : type.getDeclaredMethods()) {
                for (ComponentDecorator decorator : decorators) {
                    if (decorator.supportsMethod(definition, method)) {
                        methodDecorators.computeIfAbsent(method, k -> new ArrayList<>()).add(decorator);
                    }
                }
            }
        }
        fieldDecorators.keySet().forEach(field -> field.setAccessible(true));
        methodDecorators.keySet().forEach(method -> method.setAccessible(true));
        return instance -> {
            for (Map.Entry<Field, List<ComponentDecorator>> entry : fieldDecorators.entrySet()) {
                Field field = entry.getKey();
                for (ComponentDecorator decorator : entry.getValue()) {
                    decorator.decorateField(instance, field);
                }
            }
            for (Map.Entry<Method, List<ComponentDecorator>> entry : methodDecorators.entrySet()) {
                Method method = entry.getKey();
                for (ComponentDecorator decorator : entry.getValue()) {
                    decorator.decorateMethod(instance, method);
                }
            }
        };
    }
}
