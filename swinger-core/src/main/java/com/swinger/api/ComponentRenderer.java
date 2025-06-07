package com.swinger.api;

public interface ComponentRenderer {
    void render(ComponentResources resources, SwingWriter writer) throws Exception;
}
