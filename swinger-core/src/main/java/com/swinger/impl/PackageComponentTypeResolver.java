package com.swinger.impl;

import com.swinger.api.ComponentTypeResolver;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class PackageComponentTypeResolver implements ComponentTypeResolver {
    private final ClassLoader classLoader;
    private final List<String> packages;

    @Override
    public Class<?> getComponentType(String name) throws Exception {
        String upperName = Character.toUpperCase(name.charAt(0)) + name.substring(1);
        for (String packageName : packages) {
            try {
                return classLoader.loadClass(packageName + "." + upperName);
            } catch (ClassNotFoundException e) {
                // Continue searching in the next package
            }
        }
        throw new ClassNotFoundException("Class not found: " + upperName + " in packages: " + packages);
    }
}