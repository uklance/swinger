package com.swinger.impl;

import com.swinger.api.Binding;
import com.swinger.api.BindingSource;
import com.swinger.api.MemberAccessor;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PropertyBindingSource implements BindingSource {
    private final MemberAccessor memberAccessor;

    @Override
    public Binding create(String name) {
        return new Binding() {
            @Override
            public Object get(Object instance) throws Exception {
                return memberAccessor.getProperty(instance, name);
            }

            @Override
            public void set(Object instance, Object value) throws Exception {
                memberAccessor.setProperty(instance, name, value);
            }
        };
    }
}
