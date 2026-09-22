package com.swinger.model;

import com.swinger.api.SwingWriter;

public interface RenderCommand {
    void render(SwingWriter writer) throws Exception;
}
