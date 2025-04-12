package com.swinger;

import java.lang.reflect.Field;

public interface FieldResolver {
    boolean supportsField(Field field, SwingerContext context);
    Object resolveField(Field field, SwingerContext context);
}
