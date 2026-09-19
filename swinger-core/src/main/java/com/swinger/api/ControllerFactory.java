package com.swinger.api;

public interface ControllerFactory {
    Controller create(Class<?> componentType);
}
