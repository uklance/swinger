package com.swinger.impl;

import com.swinger.annotation.Property;
import com.swinger.api.Binding;
import com.swinger.api.ComponentInstance;
import com.swinger.api.PropertyDefinition;

import java.lang.reflect.Field;

public class FieldPropertyDefinition implements PropertyDefinition {
    private final Field field;
    private final String defaultBindingPrefix;

    public FieldPropertyDefinition(Field field, Property annotation) {
        this.field = field;
        this.defaultBindingPrefix = annotation.defaultBindingPrefix().isEmpty() ? null : annotation.defaultBindingPrefix();
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
        field.set(instance.getInstance(), binding.get(instance.getRootInstance()));
    }
}
