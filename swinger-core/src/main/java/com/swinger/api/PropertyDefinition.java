package com.swinger.api;

public interface PropertyDefinition {
    String getName();
    Class<?> getType();
    String getDefaultBindingPrefix();
    void apply(ComponentInstance instance, Binding binding);
}
