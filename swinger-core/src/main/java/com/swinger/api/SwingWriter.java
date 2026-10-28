package com.swinger.api;

import java.awt.*;
import java.util.function.Consumer;

public interface SwingWriter {
    ComponentInstance getRootInstance();
    void startComponent(ComponentInstance component);
    void endComponent();
    void startChildListener(Consumer<ComponentInstance> childListener);
    void endChildListener();
    void startElement(Component component, Object constraints);
    void endElement();
}
