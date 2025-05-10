package com.swinger.api;

import com.swinger.Swinger;
import com.swinger.model.ComponentResources;

public interface ComponentFactory {
    ComponentResources create(Swinger swinger, String tagName, String id, Object constraints) throws Exception;
}
