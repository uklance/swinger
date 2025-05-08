package com.swinger.impl;

import com.swinger.api.Binding;
import com.swinger.api.BindingRegistry;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class DefaultBindingRegistry implements BindingRegistry {
    private final Map<String, Binding> bindings;

    @Override
    public Binding get(String name) {
        Binding binding = bindings.get(name);
        if (binding == null) {
            throw new RuntimeException(String.format("No binding registered for '%s'", name));
        }
        return binding;
    }
}