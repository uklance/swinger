package com.swinger.api;

import java.util.List;

public interface ComponentInstanceSource {
    ComponentInstance create(
            ComponentDefinition definition,
            List<PropertyBinding> properties,
            Binding keyBinding,
            ComponentInstance rootInstance,
            ComponentInstances renderedChildren
    ) throws Exception;
}
