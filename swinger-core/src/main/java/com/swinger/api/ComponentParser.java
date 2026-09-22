package com.swinger.api;

import com.swinger.model.RenderCommand;

public interface ComponentParser {
    RenderCommand parse(Class<?> type) throws Exception;
}
