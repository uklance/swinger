package com.swinger.api;

import com.swinger.model.ComponentResources;
import org.xml.sax.Locator;

public interface ComponentFactory {
    ComponentResources create(String tagName, String id, Object constraints, Locator locator);
}
