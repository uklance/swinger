package com.swinger.api;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface ComponentDecorator {
    default boolean supportsField(ComponentDefinition definition, Field field) {
        return false;
    }
    default void decorateField(ComponentInstance componentInstance, Field field) throws Exception {
        throw new UnsupportedOperationException("decorateField not implemented");
    }
    default boolean supportsMethod(ComponentDefinition definition, Method method) {
        return false;
    }
    default void decorateMethod(ComponentInstance componentInstance, Method method) throws Exception {
        throw new UnsupportedOperationException("decorateMethod not implemented");
    }
}
