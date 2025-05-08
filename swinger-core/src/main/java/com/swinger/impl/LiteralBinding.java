package com.swinger.impl;

import com.swinger.api.Binding;

public class LiteralBinding implements Binding {
    @Override
    public Object resolve(Object controller, String value) throws Exception {
        return value;
    }
}
