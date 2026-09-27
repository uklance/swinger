package com.swinger.impl;

import com.swinger.api.ComponentInstance;
import com.swinger.api.SwingWriter;
import lombok.Getter;

import java.awt.*;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class DefaultSwingWriter implements SwingWriter {
    private final Deque<Consumer<ComponentInstance>> childListenerStack = new LinkedList<>();
    private final Deque<Component> elementStack = new LinkedList<>();
    @Getter private final List<Component> rootElements = new LinkedList<>();
    private ComponentInstance rootInstance;

    @Override
    public void startComponent(ComponentInstance component, Consumer<ComponentInstance> childListener) {
        if (childListenerStack.isEmpty()) {
            rootInstance = component;
        } else {
            childListenerStack.peek().accept(component);
        }
        childListenerStack.push(childListener);
    }

    @Override
    public void endComponent() {
        childListenerStack.pop();
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

    @Override
    public ComponentInstance getRootInstance() {
        return rootInstance;
    }
}
