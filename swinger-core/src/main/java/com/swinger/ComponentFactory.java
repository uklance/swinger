package com.swinger;

import org.xml.sax.Locator;

public interface ComponentFactory {
    ComponentResources create(String tagName, String id, Object constraints, Locator locator);
}
