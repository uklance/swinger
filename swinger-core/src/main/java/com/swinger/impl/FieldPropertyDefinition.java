package com.swinger.impl;

import com.swinger.annotation.Property;
import com.swinger.api.Binding;
import com.swinger.api.ComponentInstance;
import com.swinger.api.PropertyDefinition;

import java.lang.reflect.Field;

public class FieldPropertyDefinition implements PropertyDefinition {
    private final Field field;
    private final String defaultBindingPrefix;
    private final boolean isBinding;

    public FieldPropertyDefinition(Field field, Property annotation) {
        this.field = field;
        this.defaultBindingPrefix = annotation.defaultBindingPrefix().isEmpty() ? null : annotation.defaultBindingPrefix();
        this.isBinding = Binding.class == field.getType();
    }

    @Override
    public String getName() {
        return field.getName();
    }

    @Override
    public Class<?> getType() {
        return field.getType();
    }

    @Override
    public String getDefaultBindingPrefix() {
        return defaultBindingPrefix;
    }

    @Override
    public void apply(ComponentInstance instance, Binding binding) throws Exception {
        Object value = isBinding ? binding : binding.get(instance.getRootComponent());
        field.set(instance.getComponent(), value);
    }
}
