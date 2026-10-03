package com.swinger.impl;

import com.swinger.api.PropertyDefinition;
import com.swinger.api.PropertyDefinitions;

import java.util.Map;

public class DefaultPropertyDefinitions implements PropertyDefinitions {
    private final Map<String, PropertyDefinition> definitions;

    public DefaultPropertyDefinitions(Map<String, PropertyDefinition> definitions) {
        this.definitions = Map.copyOf(definitions);
    }

    @Override
    public boolean contains(String name) {
        return definitions.containsKey(name);
    }

    @Override
    public PropertyDefinition get(String name) {
        return definitions.get(name);
    }
}
