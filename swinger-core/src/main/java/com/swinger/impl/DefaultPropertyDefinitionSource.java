package com.swinger.impl;

import com.swinger.annotation.Property;
import com.swinger.annotation.ProxyProperties;
import com.swinger.api.PropertyDefinition;
import com.swinger.api.PropertyDefinitionSource;
import com.swinger.api.PropertyDefinitions;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultPropertyDefinitionSource implements PropertyDefinitionSource {
    private final Map<Class<?>, PropertyDefinitions> definitionsByType = new ConcurrentHashMap<>();

    @Override
    public PropertyDefinitions get(Class<?> type) {
        return definitionsByType.computeIfAbsent(type, this::discoverProperties);
    }

    private PropertyDefinitions discoverProperties(Class<?> type) {
        Map<String, PropertyDefinition> definitions = new LinkedHashMap<>();
        for (Class<?> current = type; current != null; current = current.getSuperclass()) {
            for (Field field : current.getDeclaredFields()) {
                if (field.isAnnotationPresent(Property.class)) {
                    field.setAccessible(true);
                    PropertyDefinition definition = new FieldPropertyDefinition(field, field.getAnnotation(Property.class));
                    definitions.putIfAbsent(definition.getName(), definition);
                }
                ProxyProperties proxyProperties = field.getAnnotation(ProxyProperties.class);
                if (proxyProperties != null) {
                    field.setAccessible(true);
                    for (PropertyDefinition definition : getProxyProperties(field, proxyProperties).values()) {
                        definitions.putIfAbsent(definition.getName(), definition);
                    }
                }
            }
        }
        return new DefaultPropertyDefinitions(definitions);
    }

    private Map<String, PropertyDefinition> getProxyProperties(Field field, ProxyProperties annotation) {
        Map<String, PropertyDefinition> properties = new LinkedHashMap<>();
        Pattern includePattern = Pattern.compile(annotation.include());
        Pattern excludePattern = annotation.exclude().isEmpty() ? null : Pattern.compile(annotation.exclude());
        for (Method method : field.getType().getMethods()) {
            if (method.getParameterCount() == 1) {
                Matcher matcher = includePattern.matcher(method.getName());
                if (matcher.matches() && (excludePattern == null || !excludePattern.matcher(method.getName()).matches())) {
                    PropertyDefinition definition =
                            new ProxyPropertyDefinition(field, method, annotation, matcher.group(1));
                    properties.putIfAbsent(definition.getName(), definition);
                }
            }
        }
        return properties;
    }
}
