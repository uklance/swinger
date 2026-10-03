package com.swinger.api;

public interface PropertyDefinitionSource {
    PropertyDefinitions get(Class<?> type);
}
