package com.swinger.impl;

import com.swinger.api.BindingSource;
import com.swinger.api.BindingSourceRegistry;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class DefaultBindingSourceRegistry implements BindingSourceRegistry {
    private final Map<String, BindingSource> bindingsSources;

    @Override
    public BindingSource get(String name) {
        BindingSource bindingSource = bindingsSources.get(name);
        if (bindingSource == null) {
            throw new RuntimeException(String.format("No bindingSource registered for '%s'", name));
        }
        return bindingSource;
    }
}