package com.swinger.impl;

import com.swinger.annotation.ProxyProperties;
import com.swinger.api.Binding;
import com.swinger.api.ComponentInstance;
import com.swinger.api.PropertyDefinition;
import com.swinger.api.PropertyValueConverter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static java.beans.Introspector.decapitalize;

public class ProxyPropertyDefinition implements PropertyDefinition {
    private final String name;
    private final Class<?> type;
    private final Field field;
    private final Method setter;
    private final String defaultBindingPrefix;
    private final boolean isBinding;
    private final PropertyValueConverter valueConverter;

    public ProxyPropertyDefinition(
            Field field,
            Method setter,
            ProxyProperties annotation,
            String nameGroup,
            PropertyValueConverter valueConverter
    ) {
        this.field = field;
        this.setter = setter;
        this.defaultBindingPrefix = annotation.defaultBindingPrefix().isEmpty() ? null : annotation.defaultBindingPrefix();
        this.type = setter.getParameterTypes()[0];
        this.name = annotation.prefix().isEmpty()
                ? decapitalize(nameGroup)
                : annotation.prefix() + nameGroup;
        this.isBinding = Binding.class == type;
        this.valueConverter = valueConverter;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public String getDefaultBindingPrefix() {
        return defaultBindingPrefix;
    }

    @Override
    public void apply(ComponentInstance instance, Binding binding) throws Exception {
        Object proxy = field.get(instance.getComponent());
        Object value = isBinding ? binding : binding.get(instance.getRootInstance().getComponent());
        if (!isBinding) {
            value = valueConverter.convert(value, type);
        }
        setter.invoke(proxy, value);
    }
}
