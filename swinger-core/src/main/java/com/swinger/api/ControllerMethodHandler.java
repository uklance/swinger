package com.swinger.api;

import java.lang.reflect.Method;

public interface ControllerMethodHandler {
    boolean supportsMethod(Method method);
    void handleMethod(Method method, Controller controller);
}
