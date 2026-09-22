package com.swinger.api;

import com.swinger.model.RenderCommand;

import java.util.List;

public interface ComponentDefinition {
    Class<?> getType();
    String getId();
    ComponentInstance createInstance(List<PropertyBinding> properties);
    List<PropertyDefinition> getPropertyDefinitions();
    Controller getController();
    RenderCommand body();
    RenderCommand template();
}
