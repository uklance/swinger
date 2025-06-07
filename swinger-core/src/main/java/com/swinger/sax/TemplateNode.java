package com.swinger.sax;

import com.swinger.model.Location;
import org.xml.sax.Attributes;

public interface TemplateNode {
    enum Type {
        COMPONENT, PARAMETER;
    }

    String getName();
    Attributes getAttributes();
    Location getLocation();
    String getText();
    Type getType();
}
