package com.swinger.impl;

import com.swinger.Swinger;
import com.swinger.api.ComponentSource;
import com.swinger.model.ComponentResources;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SwingerComponentSource implements ComponentSource {
    private final Class<?> controllerType;

    @Override
    public ComponentResources create(Swinger swinger, String id, Object constraints) throws Exception {
        return swinger.createComponent(controllerType);
    }
}
