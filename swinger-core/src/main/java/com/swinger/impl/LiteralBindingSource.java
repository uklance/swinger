package com.swinger.impl;

import com.swinger.api.Binding;
import com.swinger.api.BindingSource;

public class LiteralBindingSource implements BindingSource {
    @Override
    public Binding create(String value) {
        return instance -> value;
    }
}
