package com.swinger.impl;

import com.swinger.annotation.Property;
import com.swinger.api.Binding;
import com.swinger.api.ComponentInstance;
import com.swinger.api.PropertyDefinition;
import com.swinger.api.PropertyValueConverter;

import java.lang.reflect.Field;

public class FieldPropertyDefinition implements PropertyDefinition {
    private final Field field;
    private final String defaultBindingPrefix;
    private final boolean isBinding;
    private final PropertyValueConverter valueConverter;

    public FieldPropertyDefinition(Field field, Property annotation, PropertyValueConverter valueConverter) {
        this.field = field;
        this.defaultBindingPrefix = annotation.defaultBindingPrefix().isEmpty() ? null : annotation.defaultBindingPrefix();
        this.isBinding = Binding.class == field.getType();
        this.valueConverter = valueConverter;
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
        if (!isBinding) {
            value = valueConverter.convert(value, field.getType());
        }
        field.set(instance.getComponent(), value);
    }
}
