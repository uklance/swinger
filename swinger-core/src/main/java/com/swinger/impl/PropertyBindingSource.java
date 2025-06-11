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
            @Override
            public Object get() throws Exception {
                return memberAccessor.getProperty(resources.getRoot().getController(), name);
            }

            @Override
            public void set(Object value) throws Exception {
                memberAccessor.setProperty(resources.getRoot().getController(), name, value);
            }
        };
    }
}
