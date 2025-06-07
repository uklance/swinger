package com.swinger.api;

import java.lang.reflect.Field;

public interface ControllerFieldHandler {
    boolean supportsField(Field field);
    void handleField(Field field, Controller controller);
}
