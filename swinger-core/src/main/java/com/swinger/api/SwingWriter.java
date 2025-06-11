package com.swinger.api;

import java.awt.*;

public interface SwingWriter {
    void push(Component component);
    void push(Component component, Object constraints);
    Component pop();
    int depth();
}
