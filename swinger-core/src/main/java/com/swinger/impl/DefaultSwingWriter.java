package com.swinger.impl;

import com.swinger.api.ComponentInstance;
import com.swinger.api.SwingWriter;

import java.awt.*;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class DefaultSwingWriter implements SwingWriter {
    private final Deque<ComponentInstance> componentStack = new LinkedList<>();
    private final Deque<Component> elementStack = new LinkedList<>();
    private final List<Component> rootElements = new LinkedList<>();

    @Override
    public void startComponent(ComponentInstance component) {
        componentStack.push(component);
    }

    @Override
    public void endComponent() {
        componentStack.pop();
    }

    @Override
    public void startElement(Component component, Object constraints) {
        if (elementStack.isEmpty()) {
            if (constraints != null) {
                throw new IllegalArgumentException("Constraints cannot be specified for root elements.");
            }
            rootElements.add(component);
        } else {
            Container container = (Container) elementStack.getLast();
            container.add(component, constraints);
        }
        elementStack.push(component);
    }

    @Override
    public void endElement() {
        elementStack.pop();
    }

    @Override
    public int elementDepth() {
        return elementStack.size();
    }
}
