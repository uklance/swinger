package com.swinger.impl;

import com.swinger.api.Binding;
import com.swinger.api.BindingSource;
import com.swinger.api.ComponentInstance;
import com.swinger.api.MemberAccessor;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PropertyBindingSource implements BindingSource {
    private final MemberAccessor memberAccessor;

    @Override
    public Binding create(String name) {
        return new Binding() {
            @Override
            public Object get(ComponentInstance instance) throws Exception {
                return memberAccessor.getProperty(instance.getInstance(), name);
            }

            @Override
            public void set(ComponentInstance instance, Object value) throws Exception {
                memberAccessor.setProperty(instance.getInstance(), name, value);
            }
        };
    }
}
