package com.swinger;

import lombok.AllArgsConstructor;
import org.xml.sax.Locator;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class DefaultComponentFactory implements ComponentFactory {
    private Map<String, ComponentSource> componentSources = new HashMap<>();

    @Override
    public ComponentResources create(String tagName, String id, Object constraints, Locator locator) {
        ComponentSource componentSource = componentSources.get(tagName);
        if (componentSource == null) {
            throw new RuntimeException(String.format("Unsupported tag '%s'", tagName));
        }
        return componentSource.create(id, constraints, locator);
    }
}
