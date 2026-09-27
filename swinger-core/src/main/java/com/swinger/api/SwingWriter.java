package com.swinger.api;

import java.awt.*;
import java.util.function.Consumer;

public interface SwingWriter {
    ComponentInstance getRootInstance();
    void startComponent(ComponentInstance component, Consumer<ComponentInstance> childListener);
    void endComponent();
    void startElement(Component component, Object constraints);
    void endElement();
    int elementDepth();
}
