package com.swinger.io;

import java.io.IOException;
import java.io.InputStream;

public interface Resource {
    String getName();
    String getPath();
    InputStream getInputStream() throws IOException;
    default boolean exists() throws IOException {
        try (InputStream in = getInputStream()) {
            return in != null;
        }
    }
}
