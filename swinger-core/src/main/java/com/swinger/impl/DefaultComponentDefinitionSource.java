package com.swinger.impl;

import com.swinger.annotation.Property;
import com.swinger.annotation.ProxyProperties;
import com.swinger.api.*;
import com.swinger.model.RenderCommand;
import lombok.AllArgsConstructor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
public class DefaultComponentDefinitionSource implements ComponentDefinitionSource {
    private final ControllerSource controllerSource;

    @Override
    public ComponentDefinition get(Class<?> type, String id, Binding keyBinding, RenderCommand template, RenderCommand body) throws Exception {
        List<PropertyDefinition> propertyDefinitions = getProperties(type);
        Controller controller = controllerSource.get(type);

        return new ComponentDefinition() {
            @Override
            public Class<?> getType() {
                return type;
            }

            @Override
            public String getId() {
                return id;
            }

            @Override
            public ComponentInstance createInstance(List<PropertyBinding> properties, ComponentInstance rootInstance, ComponentInstances renderedChildren) throws Exception {
                return new DefaultComponentInstance(this, properties, keyBinding, rootInstance, renderedChildren);
            }

            @Override
            public List<PropertyDefinition> getPropertyDefinitions() {
                return propertyDefinitions;
            }

            @Override
            public Controller getController() {
                return controller;
            }

            @Override
            public RenderCommand body() {
                return body;
            }

            @Override
            public RenderCommand template() {
                return template;
            }
        };
    }

    private List<PropertyDefinition> getProperties(Class<?> type) {
        List<PropertyDefinition> properties = new ArrayList<>();
        for (Class<?> current = type; current != null; current = current.getSuperclass()) {
            Field[] fields = current.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Property.class)) {
                    field.setAccessible(true);
                    properties.add(new FieldPropertyDefinition(field, field.getAnnotation(Property.class)));
                }
                if (field.isAnnotationPresent(ProxyProperties.class)) {
                    properties.addAll(getProxyProperties(field, field.getAnnotation(ProxyProperties.class)));
                }
            }
        }
        return properties;
    }

    private List<PropertyDefinition> getProxyProperties(Field field, ProxyProperties annotation) {
        List<PropertyDefinition> properties = new ArrayList<>();
        Pattern includePattern = Pattern.compile(annotation.include());
        for (Method method : field.getType().getMethods()) {
            if (method.getParameterCount() == 1) {
                Matcher matcher = includePattern.matcher(method.getName());
                if (matcher.matches()) {
                    properties.add(new ProxyPropertyDefinition(field, method, annotation, matcher.group(1)));
                }
            }
        }
        return properties;
    }
}
