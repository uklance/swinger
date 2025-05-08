package com.swinger.impl;

import com.swinger.api.ComponentFactory;
import com.swinger.api.ComponentSource;
import com.swinger.model.ComponentResources;
import lombok.AllArgsConstructor;
import org.xml.sax.Locator;

import java.util.Map;

@AllArgsConstructor
public class DefaultComponentFactory implements ComponentFactory {
    private final Map<String, ComponentSource> componentSources;

    @Override
    public ComponentResources create(String tagName, String id, Object constraints, Locator locator) throws Exception {
        ComponentSource componentSource = componentSources.get(tagName);
        if (componentSource == null) {
            throw new RuntimeException(String.format("Unsupported tag '%s'", tagName));
        }
        ComponentResources resources = componentSource.create(id, constraints, locator);
        Object controller = resources.getController();

        return resources;
    }

}
