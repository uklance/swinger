package com.swinger.impl;

import com.swinger.api.ComponentTypeResolver;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PackageComponentTypeResolver implements ComponentTypeResolver {
    private final ClassLoader classLoader;
    private final String packageName;

    @Override
    public Class<?> getComponentType(String name) throws Exception {
        return classLoader.loadClass(packageName + "." + name);
    }
}