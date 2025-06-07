package com.swinger.api;

public interface ComponentParser {
    ComponentResources parse(Class<? extends Controller> type) throws Exception;
}
