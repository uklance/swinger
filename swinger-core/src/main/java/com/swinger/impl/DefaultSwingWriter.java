package com.swinger.impl;

import com.swinger.api.SwingWriter;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class DefaultSwingWriter implements SwingWriter {
    private final LinkedList<Component> stack = new LinkedList<>();
    private final List<Component> roots = new LinkedList<>();

    @Override
    public void push(Component component) {
        push(component, null);
    }

    @Override
    public void push(Component component, Object constraints) {
        if (stack.isEmpty()) {
            roots.add(component);
        } else {
            Container container = (Container) stack.peek();
            container.add(component, constraints);
        }
        stack.push(component);
    }

    @Override
    public Component pop() {
        return stack.pop();
    }

    @Override
    public int depth() {
        return stack.size();
    }

    public List<Component> getRoots() {
        return roots;
    }
}
