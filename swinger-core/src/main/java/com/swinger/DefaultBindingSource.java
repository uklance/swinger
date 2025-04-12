package com.swinger;

import lombok.AllArgsConstructor;

import java.util.Map;
import java.util.Objects;

@AllArgsConstructor
public class DefaultBindingSource implements BindingSource {
    private Map<String, Binding> bindingMap;
    private Binding defaultBinding;

    @Override
    public Binding getBinding(String name) {
        Binding binding = bindingMap.get(name);
        Objects.requireNonNull(binding, "No binding for " + name);
        return binding;
    }

    @Override
    public Binding getDefaultBinding() {
        return defaultBinding;
    }
}
