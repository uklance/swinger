package com.swinger.impl;

import com.swinger.api.Binding;
import com.swinger.api.BindingSource;

public class NewBindingSource implements BindingSource {
    @Override
    public Binding create(String value) {
        return instance -> Class.forName(value).getConstructor().newInstance();
    }
}
