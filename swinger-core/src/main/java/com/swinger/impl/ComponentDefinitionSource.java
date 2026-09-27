package com.swinger.impl;

import com.swinger.api.Binding;
import com.swinger.api.ComponentDefinition;
import com.swinger.model.RenderCommand;

public interface ComponentDefinitionSource {
    ComponentDefinition get(Class<?> type, String id, Binding key, RenderCommand template, RenderCommand body) throws Exception;
}
