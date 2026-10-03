package com.swinger.impl;

import com.swinger.api.*;
import com.swinger.model.RenderCommand;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class DefaultComponentDefinitionSource implements ComponentDefinitionSource {
    private final ControllerSource controllerSource;
    private final PropertyDefinitionSource propertyDefinitionSource;

    @Override
    public ComponentDefinition get(Class<?> type, String id, Binding keyBinding, RenderCommand template, RenderCommand body) throws Exception {
        PropertyDefinitions propertyDefinitions = propertyDefinitionSource.get(type);
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
            public PropertyDefinitions getPropertyDefinitions() {
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

}
