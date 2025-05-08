package com.swinger.impl;

import com.swinger.model.ComponentResources;
import com.swinger.api.ComponentSource;
import lombok.AllArgsConstructor;
import org.xml.sax.Locator;

import java.awt.*;
import java.util.function.Supplier;

@AllArgsConstructor
public class SupplierComponentSource implements ComponentSource {
    private final Supplier<Component> supplier;

    @Override
    public ComponentResources create(String id, Object constraints, Locator locator) {
        Component component = supplier.get();
        return new ComponentResources(id, component, component, constraints);
    }
}
