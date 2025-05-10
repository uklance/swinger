package com.swinger.impl;

import com.swinger.Swinger;
import com.swinger.api.ComponentSource;
import com.swinger.model.ComponentResources;
import lombok.AllArgsConstructor;

import java.awt.*;
import java.util.function.Supplier;

@AllArgsConstructor
public class SupplierComponentSource implements ComponentSource {
    private final Supplier<Component> supplier;

    @Override
    public ComponentResources create(Swinger swinger, String id, Object constraints) {
        Component component = supplier.get();
        return new ComponentResources(id, component, component, constraints);
    }
}
