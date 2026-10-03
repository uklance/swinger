package com.swinger.impl;

import com.swinger.annotation.ProxyProperties;
import com.swinger.api.Binding;
import com.swinger.api.ComponentInstance;
import com.swinger.api.PropertyDefinition;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static java.beans.Introspector.decapitalize;

public class ProxyPropertyDefinition implements PropertyDefinition {
    private final String name;
    private final Class<?> type;
    private final Field field;
    private final Method setter;
    private final String defaultBindingPrefix;

    public ProxyPropertyDefinition(Field field, Method setter, ProxyProperties annotation, String nameGroup) {
        this.field = field;
        this.setter = setter;
        this.defaultBindingPrefix = annotation.defaultBindingPrefix().isEmpty() ? null : annotation.defaultBindingPrefix();
        this.type = setter.getParameterTypes()[0];
        this.name = annotation.prefix().isEmpty()
                ? decapitalize(nameGroup)
                : annotation.prefix() + nameGroup;
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
        setter.invoke(proxy, binding.get(instance.getRootInstance().getComponent()));
    }
}
