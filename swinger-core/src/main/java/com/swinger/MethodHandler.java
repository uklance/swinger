package com.swinger;

import java.lang.reflect.Method;

public interface MethodHandler {
    boolean supportsMethod(Method method, SwingerContext context);
    void handleMethod(Method method, SwingerContext context);
}
