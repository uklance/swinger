package com.swinger.api;

public interface ControllerSource {
    Controller get(Class<?> type) throws Exception;
}
