package com.swinger.api;

import com.swinger.model.ComponentResources;
import org.xml.sax.Locator;

public interface ComponentSource {
    ComponentResources create(String id, Object constraints, Locator locator);
}
