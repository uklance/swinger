package com.swinger.io;

import java.io.IOException;
import java.io.InputStream;

public interface Resource {
    String getName();
    String getPath();
    InputStream getInputStream() throws IOException;
    boolean exists() throws IOException;
}
