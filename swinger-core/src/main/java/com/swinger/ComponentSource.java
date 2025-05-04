package com.swinger;

import org.xml.sax.Locator;

public interface ComponentSource {
    ComponentResources create(String id, Object constraints, Locator locator);
}
