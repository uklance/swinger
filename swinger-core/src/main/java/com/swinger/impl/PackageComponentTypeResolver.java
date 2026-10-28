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
        String[] candidates = getCandidates(name);
        for (String packageName : packages) {
            for (String candidate : candidates) {
                try {
                    return classLoader.loadClass(packageName + "." + candidate);
                } catch (ClassNotFoundException e) {
                    // Ignore and try the next candidate
                }
            }
        }
        throw new ClassNotFoundException("Class not found: " + name + " in packages: " + packages);
    }

    private String[] getCandidates(String name) {
        String upperName = Character.toUpperCase(name.charAt(0)) + name.substring(1);
        int dotIndex = upperName.lastIndexOf('.');
        if (dotIndex == -1) {
            return new String[] { upperName };
        } else {
            return new String[] { upperName, upperName.substring(0, dotIndex) + '$' + Character.toUpperCase(upperName.charAt(dotIndex + 1)) + upperName.substring(dotIndex + 2) };
        }
    }
}