package com.swinger.api;

import java.awt.*;

public interface SwingWriter {
    void startComponent(ComponentInstance component);
    void endComponent();
    void startElement(Component component, Object constraints);
    void endElement();
    int elementDepth();
}
