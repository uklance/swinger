package com.swinger.impl;

import com.swinger.api.Binding;
import com.swinger.api.BindingSource;
import com.swinger.api.MemberAccessor;
import lombok.AllArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
public class PropertyBindingSource implements BindingSource {
    private final MemberAccessor memberAccessor;

    @Override
    public Binding create(String name) {
        String[] path = Arrays.stream(name.split("\\.", -1))
                .map(String::trim)
                .toArray(String[]::new);
        if (path.length == 0 || Arrays.stream(path).anyMatch(String::isEmpty)) {
            throw new IllegalArgumentException("Invalid property path: " + name);
        }
        return new Binding() {
            @Override
            public Object get(Object instance) throws Exception {
                Object target = instance;
                for (String segment : path) {
                    if (target == null) {
                        return null;
                    }
                    target = memberAccessor.getProperty(target, segment);
                }
                return target;
            }

            @Override
            public void set(Object instance, Object value) throws Exception {
                Object target = instance;
                for (int i = 0; i < path.length - 1; i++) {
                    target = memberAccessor.getProperty(target, path[i]);
                    if (target == null) {
                        throw new IllegalStateException("Cannot set property path " + name + ": " + path[i] + " is null");
                    }
                }
                memberAccessor.setProperty(target, path[path.length - 1], value);
            }
        };
    }
}
