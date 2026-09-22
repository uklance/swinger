package com.swinger.impl;

import com.swinger.api.Binding;
import com.swinger.api.ComponentDefinition;
import com.swinger.model.RenderCommand;

public interface ComponentDefinitionSource {
    ComponentDefinition get(Class<?> type, RenderCommand body, String id, Binding key);
}
