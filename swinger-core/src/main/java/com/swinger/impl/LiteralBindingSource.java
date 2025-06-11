package com.swinger.impl;

import com.swinger.api.Binding;
import com.swinger.api.BindingSource;
import com.swinger.api.ComponentResources;
import com.swinger.api.Controller;

public class LiteralBindingSource implements BindingSource {
    @Override
    public Binding create(String value, ComponentResources resources) {
        return () -> value;
    }
}
