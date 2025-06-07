package com.swinger.io;

import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.InputStream;

@AllArgsConstructor
public class ClassloaderResource implements Resource {
    private final ClassLoader classLoader;
    private final String path;

    public ClassloaderResource(Class<?> type, String path) {
        this(type.getClassLoader(), path);
    }

    @Override
    public String getName() {
        int slashIndex = path.lastIndexOf('/');
        return slashIndex < 0 ? path : path.substring(slashIndex + 1);
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return classLoader.getResourceAsStream(path);
    }

    @Override
    public boolean exists() throws IOException {
        try (InputStream in = getInputStream()) {
            return in != null;
        }
    }
}
