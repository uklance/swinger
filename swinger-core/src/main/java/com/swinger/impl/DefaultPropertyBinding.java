package com.swinger.impl;

import com.swinger.api.Binding;
import com.swinger.api.PropertyBinding;
import com.swinger.model.RenderCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DefaultPropertyBinding implements PropertyBinding {
    private final String name;
    private final Binding binding;
}
