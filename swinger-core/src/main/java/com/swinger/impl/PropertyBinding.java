package com.swinger.impl;

import com.swinger.api.Binding;
import com.swinger.api.MemberAccessor;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PropertyBinding implements Binding {
    private final MemberAccessor memberAccessor;

    @Override
    public Object resolve(Object controller, String value) throws Exception {
        return memberAccessor.getProperty(controller, value);
    }
}
