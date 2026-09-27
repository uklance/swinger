package com.swinger.impl;

import com.swinger.api.Binding;
import com.swinger.api.PropertyBinding;
import com.swinger.api.PropertyDefinition;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DefaultPropertyBinding implements PropertyBinding {
    private final PropertyDefinition definition;
    private final Binding binding;
}
