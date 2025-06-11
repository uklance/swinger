package com.swinger.impl;

import com.swinger.api.ComponentResources;
import com.swinger.api.Controller;
import com.swinger.sax.ComponentTemplate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DefaultComponentResources implements ComponentResources {
    private final ComponentResources parent;
    private final Controller controller;
    private final ComponentTemplate template;

    @Override
    public ComponentResources getRoot() {
        ComponentResources current = this;
        while (current.getParent() != null) {
            current = current.getParent();
        }
        return current;
    }
}
