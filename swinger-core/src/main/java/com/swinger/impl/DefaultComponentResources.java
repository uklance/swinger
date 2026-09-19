package com.swinger.impl;

import com.swinger.api.ComponentResources;
import com.swinger.api.Controller;
import com.swinger.sax.ComponentTemplate;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DefaultComponentResources implements ComponentResources {
    private final ComponentResources parent;
    private final Object componentInstance;
    private final Controller controller;
    private final ComponentTemplate template;

    @Override
    public ComponentResources getParent() {
        return parent;
    }

    @Override
    public ComponentTemplate getTemplate() {
        return template;
    }

    @Override
    public Object getComponentInstance() {
        return componentInstance;
    }

    @Override
    public Controller getController() {
        return controller;
    }

    @Override
    public ComponentResources getRoot() {
        ComponentResources current = this;
        while (current.getParent() != null) {
            current = current.getParent();
        }
        return current;
    }
}
