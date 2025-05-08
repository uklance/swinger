package com.swinger.impl;

import com.swinger.api.Binding;

public class NewBinding implements Binding {
    @Override
    public Object resolve(Object controller, String value) throws Exception {
        return Class.forName(value).getConstructor().newInstance();
    }
}
