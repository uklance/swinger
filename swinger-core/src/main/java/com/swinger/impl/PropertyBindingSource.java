package com.swinger.impl;

import com.swinger.api.Binding;
import com.swinger.api.BindingSource;
import com.swinger.api.ComponentResources;
import com.swinger.api.MemberAccessor;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PropertyBindingSource implements BindingSource {
    private final MemberAccessor memberAccessor;

    @Override
    public Binding create(String name, ComponentResources resources) {
        return new Binding() {
            private Object target() {
                return resources.getRoot().getComponentInstance();
            }

            @Override
            public Object get() throws Exception {
                return memberAccessor.getProperty(target(), name);
            }

            @Override
            public void set(Object value) throws Exception {
                memberAccessor.setProperty(target(), name, value);
            }
        };
    }
}
