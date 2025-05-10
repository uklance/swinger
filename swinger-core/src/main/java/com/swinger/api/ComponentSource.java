package com.swinger.api;

import com.swinger.Swinger;
import com.swinger.model.ComponentResources;

public interface ComponentSource {
    ComponentResources create(Swinger swinger, String id, Object constraints) throws Exception;
}
